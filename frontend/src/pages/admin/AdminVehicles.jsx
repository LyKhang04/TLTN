import React, { useEffect, useState } from "react";
import { Plus, Trash2, Car, CreditCard } from "lucide-react";
import { C } from "../../theme";
import { Shell, Card, Th, Td, StatusBadge, PrimaryButton, TextField, LoadingBlock, ErrorBlock } from "../../components/Common";
import {
  getVehicles, createVehicle, deleteVehicle,
  getResidentCards, createResidentCard, deleteResidentCard,
  getApartments, getUsers,
} from "../../api/services";

const TABS = [
  { key: "vehicles", label: "Phương tiện", icon: Car },
  { key: "cards", label: "Thẻ cư dân", icon: CreditCard },
];

export default function AdminVehicles() {
  const [tab, setTab] = useState("vehicles");
  const [apartments, setApartments] = useState([]);
  const [users, setUsers] = useState([]);

  useEffect(() => {
    Promise.all([getApartments(), getUsers()])
      .then(([a, u]) => { setApartments(a || []); setUsers(u || []); })
      .catch(() => {});
  }, []);

  return (
    <Shell eyebrow="Cư dân" title="Phương tiện & Thẻ cư dân">
      <div style={{ display: "flex", gap: 8, marginBottom: 16 }}>
        {TABS.map((t) => {
          const Icon = t.icon;
          const active = tab === t.key;
          return (
            <button key={t.key} onClick={() => setTab(t.key)}
              style={{
                display: "flex", alignItems: "center", gap: 7, cursor: "pointer",
                padding: "9px 15px", borderRadius: 8, fontSize: 13.5,
                border: `1px solid ${active ? C.ink : C.line}`,
                background: active ? C.ink : C.paper,
                color: active ? "#fff" : C.inkSoft,
                fontWeight: active ? 600 : 400,
              }}>
              <Icon size={15} /> {t.label}
            </button>
          );
        })}
      </div>

      {tab === "vehicles" && <VehiclesTab apartments={apartments} users={users} />}
      {tab === "cards" && <CardsTab apartments={apartments} users={users} />}
    </Shell>
  );
}

/* ---------- Phương tiện ---------- */
function VehiclesTab({ apartments, users }) {
  const [rows, setRows] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [busy, setBusy] = useState(false);

  const [plateNumber, setPlateNumber] = useState("");
  const [vehicleType, setVehicleType] = useState("MOTORBIKE");
  const [apartmentId, setApartmentId] = useState("");
  const [userId, setUserId] = useState("");

  const load = async () => {
    setLoading(true); setError(null);
    try { setRows(await getVehicles()); }
    catch (e) { setError(e.message); }
    finally { setLoading(false); }
  };
  useEffect(() => { load(); }, []);

  const submit = async () => {
    if (!plateNumber.trim() || !apartmentId) {
      alert("Vui lòng nhập biển số và chọn căn hộ.");
      return;
    }
    setBusy(true);
    try {
      await createVehicle({
        plateNumber: plateNumber.trim(),
        vehicleType,
        status: "ACTIVE",
        apartment: { id: Number(apartmentId) },
        user: userId ? { id: Number(userId) } : null,
        registeredAt: new Date().toISOString().slice(0, 19),
      });
      setPlateNumber(""); setUserId("");
      await load();
    } catch (e) { alert("Lưu thất bại: " + e.message); }
    finally { setBusy(false); }
  };

  const remove = async (id) => {
    if (!window.confirm("Xóa phương tiện này?")) return;
    try { await deleteVehicle(id); await load(); }
    catch (e) { alert("Xóa thất bại: " + e.message); }
  };

  return (
    <>
      <Card style={{ padding: 20, marginBottom: 16 }}>
        <div style={{ fontWeight: 700, marginBottom: 6 }}>Đăng ký phương tiện</div>
        <div style={{ fontSize: 13, color: C.slate, marginBottom: 14 }}>
          Số phương tiện của mỗi căn hộ được dùng để tính phí gửi xe
          khi phát hành hóa đơn hàng tháng.
        </div>
        <TextField label="Biển số" value={plateNumber}
                   onChange={(e) => setPlateNumber(e.target.value)} placeholder="59A1-123.45" />
        <Select label="Loại xe" value={vehicleType} onChange={setVehicleType}
                options={[
                  { v: "MOTORBIKE", l: "Xe máy" },
                  { v: "CAR", l: "Ô tô" },
                  { v: "BICYCLE", l: "Xe đạp" },
                ]} />
        <Select label="Căn hộ" value={apartmentId} onChange={setApartmentId}
                options={[{ v: "", l: "-- Chọn căn hộ --" },
                  ...apartments.map((a) => ({ v: a.id, l: a.code }))]} />
        <Select label="Chủ phương tiện (tùy chọn)" value={userId} onChange={setUserId}
                options={[{ v: "", l: "-- Không chọn --" },
                  ...users.map((u) => ({ v: u.id, l: u.fullName }))]} />
        <PrimaryButton icon={Plus} onClick={submit} disabled={busy}>
          {busy ? "Đang lưu..." : "Đăng ký xe"}
        </PrimaryButton>
      </Card>

      {loading && <LoadingBlock />}
      {error && <ErrorBlock message={error} />}
      {rows && (
        <Card style={{ padding: 8 }}>
          <table style={{ width: "100%", borderCollapse: "collapse" }}>
            <thead><tr>
              <Th style={{ paddingLeft: 16 }}>Biển số</Th><Th>Loại</Th><Th>Căn hộ</Th>
              <Th>Chủ xe</Th><Th>Trạng thái</Th><Th></Th>
            </tr></thead>
            <tbody>
              {rows.map((v) => (
                <tr key={v.id} className="row-hover">
                  <Td style={{ paddingLeft: 16 }}>
                    <span className="f-mono" style={{ fontWeight: 600 }}>{v.plateNumber}</span>
                  </Td>
                  <Td>{VEHICLE_LABEL[v.vehicleType] || v.vehicleType}</Td>
                  <Td><span className="f-mono">{v.apartment?.code || "—"}</span></Td>
                  <Td>{v.user?.fullName || "—"}</Td>
                  <Td><StatusBadge status={v.status} /></Td>
                  <Td><IconDelete onClick={() => remove(v.id)} /></Td>
                </tr>
              ))}
            </tbody>
          </table>
        </Card>
      )}
    </>
  );
}

/* ---------- Thẻ cư dân ---------- */
function CardsTab({ apartments, users }) {
  const [rows, setRows] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [busy, setBusy] = useState(false);

  const [cardCode, setCardCode] = useState("");
  const [cardType, setCardType] = useState("RESIDENT");
  const [apartmentId, setApartmentId] = useState("");
  const [userId, setUserId] = useState("");

  const load = async () => {
    setLoading(true); setError(null);
    try { setRows(await getResidentCards()); }
    catch (e) { setError(e.message); }
    finally { setLoading(false); }
  };
  useEffect(() => { load(); }, []);

  const submit = async () => {
    if (!cardCode.trim() || !apartmentId || !userId) {
      alert("Vui lòng nhập mã thẻ, chọn căn hộ và cư dân.");
      return;
    }
    setBusy(true);
    try {
      await createResidentCard({
        cardCode: cardCode.trim(),
        cardType,
        status: "ACTIVE",
        apartment: { id: Number(apartmentId) },
        user: { id: Number(userId) },
        issuedAt: new Date().toISOString().slice(0, 19),
      });
      setCardCode(""); setUserId("");
      await load();
    } catch (e) { alert("Lưu thất bại: " + e.message); }
    finally { setBusy(false); }
  };

  const remove = async (id) => {
    if (!window.confirm("Xóa thẻ này?")) return;
    try { await deleteResidentCard(id); await load(); }
    catch (e) { alert("Xóa thất bại: " + e.message); }
  };

  return (
    <>
      <Card style={{ padding: 20, marginBottom: 16 }}>
        <div style={{ fontWeight: 700, marginBottom: 14 }}>Cấp thẻ cư dân</div>
        <TextField label="Mã thẻ" value={cardCode}
                   onChange={(e) => setCardCode(e.target.value)} placeholder="RC-0001" />
        <Select label="Loại thẻ" value={cardType} onChange={setCardType}
                options={[
                  { v: "RESIDENT", l: "Thẻ cư dân" },
                  { v: "PARKING", l: "Thẻ gửi xe" },
                  { v: "ELEVATOR", l: "Thẻ thang máy" },
                ]} />
        <Select label="Căn hộ" value={apartmentId} onChange={setApartmentId}
                options={[{ v: "", l: "-- Chọn căn hộ --" },
                  ...apartments.map((a) => ({ v: a.id, l: a.code }))]} />
        <Select label="Cư dân" value={userId} onChange={setUserId}
                options={[{ v: "", l: "-- Chọn cư dân --" },
                  ...users.map((u) => ({ v: u.id, l: u.fullName }))]} />
        <PrimaryButton icon={Plus} onClick={submit} disabled={busy}>
          {busy ? "Đang lưu..." : "Cấp thẻ"}
        </PrimaryButton>
      </Card>

      {loading && <LoadingBlock />}
      {error && <ErrorBlock message={error} />}
      {rows && (
        <Card style={{ padding: 8 }}>
          <table style={{ width: "100%", borderCollapse: "collapse" }}>
            <thead><tr>
              <Th style={{ paddingLeft: 16 }}>Mã thẻ</Th><Th>Loại</Th><Th>Căn hộ</Th>
              <Th>Cư dân</Th><Th>Trạng thái</Th><Th></Th>
            </tr></thead>
            <tbody>
              {rows.map((c) => (
                <tr key={c.id} className="row-hover">
                  <Td style={{ paddingLeft: 16 }}>
                    <span className="f-mono" style={{ fontWeight: 600 }}>{c.cardCode}</span>
                  </Td>
                  <Td>{c.cardType}</Td>
                  <Td><span className="f-mono">{c.apartment?.code || "—"}</span></Td>
                  <Td>{c.user?.fullName || "—"}</Td>
                  <Td><StatusBadge status={c.status} /></Td>
                  <Td><IconDelete onClick={() => remove(c.id)} /></Td>
                </tr>
              ))}
            </tbody>
          </table>
        </Card>
      )}
    </>
  );
}

/* ---------- Dùng chung ---------- */
const VEHICLE_LABEL = { MOTORBIKE: "Xe máy", CAR: "Ô tô", BICYCLE: "Xe đạp" };

function Select({ label, value, onChange, options }) {
  return (
    <div style={{ marginBottom: 14 }}>
      <div style={{ fontSize: 12.5, color: C.slate, marginBottom: 6 }}>{label}</div>
      <select value={value} onChange={(e) => onChange(e.target.value)}
        style={{
          width: "100%", border: `1px solid ${C.line}`, borderRadius: 8,
          padding: "10px 12px", fontSize: 13.5, outline: "none",
          boxSizing: "border-box", background: C.paper,
        }}>
        {options.map((o) => <option key={String(o.v)} value={o.v}>{o.l}</option>)}
      </select>
    </div>
  );
}

function IconDelete({ onClick }) {
  return (
    <button onClick={onClick} title="Xóa"
      style={{
        display: "flex", alignItems: "center", gap: 4, cursor: "pointer",
        border: `1px solid ${C.red}`, color: C.red, background: C.redSoft,
        borderRadius: 6, padding: "5px 9px", fontSize: 12,
      }}>
      <Trash2 size={13} /> Xóa
    </button>
  );
}
