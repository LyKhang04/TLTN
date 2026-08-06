import React, { useEffect, useState } from "react";
import { Plus, Trash2, Gauge, Tags, ScrollText } from "lucide-react";
import { C, fmtVnd } from "../../theme";
import { Shell, Card, Th, Td, PrimaryButton, TextField, LoadingBlock, ErrorBlock } from "../../components/Common";
import {
  getServicePrices, createServicePrice, deleteServicePrice,
  getUtilityReadings, createUtilityReading, deleteUtilityReading,
  getSystemLogs, getApartments,
} from "../../api/services";

const TABS = [
  { key: "prices", label: "Bảng giá dịch vụ", icon: Tags },
  { key: "readings", label: "Chỉ số điện nước", icon: Gauge },
  { key: "logs", label: "Nhật ký hệ thống", icon: ScrollText },
];

export default function AdminOperations() {
  const [tab, setTab] = useState("prices");

  return (
    <Shell eyebrow="Vận hành" title="Vận hành & Cấu hình">
      <div style={{ display: "flex", gap: 8, marginBottom: 16, flexWrap: "wrap" }}>
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

      {tab === "prices" && <PricesTab />}
      {tab === "readings" && <ReadingsTab />}
      {tab === "logs" && <LogsTab />}
    </Shell>
  );
}

/* ---------- Tab 1: Bảng giá dịch vụ ---------- */
function PricesTab() {
  const [rows, setRows] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [busy, setBusy] = useState(false);

  const [serviceName, setServiceName] = useState("");
  const [unit, setUnit] = useState("");
  const [unitPrice, setUnitPrice] = useState("");
  const [effectiveDate, setEffectiveDate] = useState("");

  const load = async () => {
    setLoading(true); setError(null);
    try { setRows(await getServicePrices()); }
    catch (e) { setError(e.message); }
    finally { setLoading(false); }
  };
  useEffect(() => { load(); }, []);

  const submit = async () => {
    if (!serviceName.trim() || !unitPrice) {
      alert("Vui lòng nhập tên dịch vụ và đơn giá.");
      return;
    }
    setBusy(true);
    try {
      await createServicePrice({
        serviceName: serviceName.trim(),
        unit: unit.trim(),
        unitPrice: Number(unitPrice),
        effectiveDate: effectiveDate || null,
      });
      setServiceName(""); setUnit(""); setUnitPrice(""); setEffectiveDate("");
      await load();
    } catch (e) { alert("Lưu thất bại: " + e.message); }
    finally { setBusy(false); }
  };

  const remove = async (id) => {
    if (!window.confirm("Xóa đơn giá này?")) return;
    try { await deleteServicePrice(id); await load(); }
    catch (e) { alert("Xóa thất bại: " + e.message); }
  };

  return (
    <>
      <Card style={{ padding: 20, marginBottom: 16 }}>
        <div style={{ fontWeight: 700, marginBottom: 6 }}>Thêm đơn giá dịch vụ</div>
        <div style={{ fontSize: 13, color: C.slate, marginBottom: 14 }}>
          Chức năng tự động phát hành hóa đơn sẽ lấy đơn giá có ngày hiệu lực gần nhất
          nhưng không vượt quá cuối kỳ tính. Nhờ vậy, khi điều chỉnh giá, các hóa đơn kỳ cũ
          vẫn giữ nguyên đơn giá tại thời điểm đó. Tên dịch vụ cần đặt đúng:
          <b> Phi quan ly, Tien dien, Tien nuoc, Phi gui xe</b>.
        </div>
        <TextField label="Tên dịch vụ" value={serviceName}
                   onChange={(e) => setServiceName(e.target.value)} placeholder="Phi quan ly" />
        <TextField label="Đơn vị tính" value={unit}
                   onChange={(e) => setUnit(e.target.value)} placeholder="m2 / kWh / m3 / xe" />
        <TextField label="Đơn giá (VNĐ)" type="number" value={unitPrice}
                   onChange={(e) => setUnitPrice(e.target.value)} placeholder="8000" />
        <TextField label="Ngày hiệu lực" type="date" value={effectiveDate}
                   onChange={(e) => setEffectiveDate(e.target.value)} />
        <PrimaryButton icon={Plus} onClick={submit} disabled={busy}>
          {busy ? "Đang lưu..." : "Thêm đơn giá"}
        </PrimaryButton>
      </Card>

      {loading && <LoadingBlock />}
      {error && <ErrorBlock message={error} />}
      {rows && (
        <Card style={{ padding: 8 }}>
          <table style={{ width: "100%", borderCollapse: "collapse" }}>
            <thead><tr>
              <Th style={{ paddingLeft: 16 }}>Dịch vụ</Th><Th>Đơn vị</Th>
              <Th>Đơn giá</Th><Th>Hiệu lực từ</Th><Th></Th>
            </tr></thead>
            <tbody>
              {rows.map((r) => (
                <tr key={r.id} className="row-hover">
                  <Td style={{ paddingLeft: 16, fontWeight: 600 }}>{r.serviceName}</Td>
                  <Td>{r.unit}</Td>
                  <Td className="f-mono">{fmtVnd(r.unitPrice)}</Td>
                  <Td className="f-mono">{r.effectiveDate || "—"}</Td>
                  <Td><IconDelete onClick={() => remove(r.id)} /></Td>
                </tr>
              ))}
            </tbody>
          </table>
        </Card>
      )}
    </>
  );
}

/* ---------- Tab 2: Chỉ số điện nước ---------- */
function ReadingsTab() {
  const [rows, setRows] = useState(null);
  const [apartments, setApartments] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [busy, setBusy] = useState(false);

  const [apartmentId, setApartmentId] = useState("");
  const [type, setType] = useState("ELECTRICITY");
  const [readingValue, setReadingValue] = useState("");
  const [readingDate, setReadingDate] = useState("");

  const load = async () => {
    setLoading(true); setError(null);
    try {
      const [rs, apts] = await Promise.all([getUtilityReadings(), getApartments()]);
      setRows(rs); setApartments(apts || []);
    } catch (e) { setError(e.message); }
    finally { setLoading(false); }
  };
  useEffect(() => { load(); }, []);

  const submit = async () => {
    if (!apartmentId || !readingValue || !readingDate) {
      alert("Vui lòng chọn căn hộ, nhập chỉ số và ngày ghi.");
      return;
    }
    setBusy(true);
    try {
      await createUtilityReading({
        apartment: { id: Number(apartmentId) },
        type,
        readingValue: Number(readingValue),
        readingDate,
      });
      setReadingValue("");
      await load();
    } catch (e) { alert("Lưu thất bại: " + e.message); }
    finally { setBusy(false); }
  };

  const remove = async (id) => {
    if (!window.confirm("Xóa chỉ số này?")) return;
    try { await deleteUtilityReading(id); await load(); }
    catch (e) { alert("Xóa thất bại: " + e.message); }
  };

  return (
    <>
      <Card style={{ padding: 20, marginBottom: 16 }}>
        <div style={{ fontWeight: 700, marginBottom: 6 }}>Ghi chỉ số điện / nước</div>
        <div style={{ fontSize: 13, color: C.slate, marginBottom: 14 }}>
          Nhập chỉ số tiêu thụ của từng căn hộ theo kỳ. Ngày ghi quyết định chỉ số
          thuộc kỳ nào khi phát hành hóa đơn.
        </div>

        <div style={{ marginBottom: 14 }}>
          <div style={{ fontSize: 12.5, color: C.slate, marginBottom: 6 }}>Căn hộ</div>
          <select value={apartmentId} onChange={(e) => setApartmentId(e.target.value)}
                  style={selectStyle}>
            <option value="">-- Chọn căn hộ --</option>
            {apartments.map((a) => (
              <option key={a.id} value={a.id}>{a.code}</option>
            ))}
          </select>
        </div>

        <div style={{ marginBottom: 14 }}>
          <div style={{ fontSize: 12.5, color: C.slate, marginBottom: 6 }}>Loại</div>
          <select value={type} onChange={(e) => setType(e.target.value)} style={selectStyle}>
            <option value="ELECTRICITY">Điện (kWh)</option>
            <option value="WATER">Nước (m3)</option>
          </select>
        </div>

        <TextField label="Chỉ số tiêu thụ" type="number" value={readingValue}
                   onChange={(e) => setReadingValue(e.target.value)} placeholder="215" />
        <TextField label="Ngày ghi" type="date" value={readingDate}
                   onChange={(e) => setReadingDate(e.target.value)} />
        <PrimaryButton icon={Plus} onClick={submit} disabled={busy}>
          {busy ? "Đang lưu..." : "Lưu chỉ số"}
        </PrimaryButton>
      </Card>

      {loading && <LoadingBlock />}
      {error && <ErrorBlock message={error} />}
      {rows && (
        <Card style={{ padding: 8 }}>
          <table style={{ width: "100%", borderCollapse: "collapse" }}>
            <thead><tr>
              <Th style={{ paddingLeft: 16 }}>Căn hộ</Th><Th>Loại</Th>
              <Th>Chỉ số</Th><Th>Ngày ghi</Th><Th></Th>
            </tr></thead>
            <tbody>
              {rows.map((r) => (
                <tr key={r.id} className="row-hover">
                  <Td style={{ paddingLeft: 16 }}>
                    <span className="f-mono">{r.apartment?.code || "—"}</span>
                  </Td>
                  <Td>{r.type === "ELECTRICITY" ? "Điện" : "Nước"}</Td>
                  <Td className="f-mono" style={{ fontWeight: 600 }}>{r.readingValue}</Td>
                  <Td className="f-mono">{r.readingDate}</Td>
                  <Td><IconDelete onClick={() => remove(r.id)} /></Td>
                </tr>
              ))}
            </tbody>
          </table>
        </Card>
      )}
    </>
  );
}

/* ---------- Tab 3: Nhật ký hệ thống ---------- */
function LogsTab() {
  const [rows, setRows] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    (async () => {
      try { setRows(await getSystemLogs()); }
      catch (e) { setError(e.message); }
      finally { setLoading(false); }
    })();
  }, []);

  return (
    <>
      {loading && <LoadingBlock />}
      {error && <ErrorBlock message={error} />}
      {rows && rows.length === 0 && (
        <Card><div style={{ padding: 20, color: C.slate }}>
          Chưa có bản ghi nhật ký nào.
        </div></Card>
      )}
      {rows && rows.length > 0 && (
        <Card style={{ padding: 8 }}>
          <table style={{ width: "100%", borderCollapse: "collapse" }}>
            <thead><tr>
              <Th style={{ paddingLeft: 16 }}>Người thực hiện</Th><Th>Hành động</Th>
              <Th>Đối tượng</Th><Th>Mã đối tượng</Th><Th>Thời gian</Th>
            </tr></thead>
            <tbody>
              {rows.map((l) => (
                <tr key={l.id} className="row-hover">
                  <Td style={{ paddingLeft: 16 }}>{l.user?.fullName || "—"}</Td>
                  <Td>{l.action}</Td>
                  <Td>{l.entity}</Td>
                  <Td className="f-mono">{l.entityId ?? "—"}</Td>
                  <Td className="f-mono">{fmtTime(l.createdAt)}</Td>
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
const selectStyle = {
  width: "100%", border: `1px solid ${C.line}`, borderRadius: 8,
  padding: "10px 12px", fontSize: 13.5, outline: "none", boxSizing: "border-box",
  background: C.paper,
};

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

function fmtTime(v) {
  if (!v) return "—";
  return String(v).replace("T", " ").slice(0, 16);
}
