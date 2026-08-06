import React, { useEffect, useState } from "react";
import { Wrench, CheckCircle2, UserCog, Plus } from "lucide-react";
import { C, fmtVnd } from "../../theme";
import { Shell, Card, Th, Td, StatusBadge, PrimaryButton, TextField, LoadingBlock, ErrorBlock } from "../../components/Common";
import {
  getMaintenanceTickets, getMaintenanceSummary, assignMaintenance,
  completeMaintenance, createMaintenanceTicket, getUsers,
} from "../../api/services";

const STATUS_LABEL = {
  PENDING: "Chờ phân công",
  IN_PROGRESS: "Đang thực hiện",
  DONE: "Hoàn thành",
  CANCELLED: "Đã hủy",
};

export default function AdminMaintenance({ currentUser }) {
  const [tickets, setTickets] = useState(null);
  const [summary, setSummary] = useState(null);
  const [technicians, setTechnicians] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [busy, setBusy] = useState(null);

  const [showForm, setShowForm] = useState(false);
  const [title, setTitle] = useState("");
  const [category, setCategory] = useState("");
  const [description, setDescription] = useState("");
  const [scheduledDate, setScheduledDate] = useState("");

  const load = async () => {
    setLoading(true);
    setError(null);
    try {
      const [list, sum, users] = await Promise.all([
        getMaintenanceTickets(),
        getMaintenanceSummary(),
        getUsers(),
      ]);
      setTickets(list);
      setSummary(sum);
      // Chỉ nhân sự kỹ thuật hoặc Ban quản lý mới được phân công thực hiện
      setTechnicians((users || []).filter(
        (u) => ["TECHNICIAN", "ADMIN"].includes(u.role?.name)
      ));
    } catch (e) {
      setError(e.message);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => { load(); }, []);

  const doAssign = async (ticket, userId) => {
    if (!userId) return;
    setBusy(ticket.id);
    try {
      await assignMaintenance(ticket.id, { assignedToId: Number(userId) });
      await load();
    } catch (e) {
      alert("Phân công thất bại: " + e.message);
    } finally {
      setBusy(null);
    }
  };

  const doComplete = async (ticket) => {
    const cost = window.prompt("Nhập chi phí bảo trì thực tế (VNĐ):", ticket.cost || "0");
    if (cost === null) return;
    setBusy(ticket.id);
    try {
      await completeMaintenance(ticket.id, { cost: Number(cost) || 0 });
      await load();
    } catch (e) {
      alert("Không hoàn thành được phiếu: " + e.message);
    } finally {
      setBusy(null);
    }
  };

  const submitNew = async () => {
    if (!title.trim()) {
      alert("Vui lòng nhập tiêu đề phiếu bảo trì.");
      return;
    }
    setBusy("new");
    try {
      await createMaintenanceTicket({
        title, category, description,
        scheduledDate: scheduledDate || null,
        status: "PENDING",
        createdBy: currentUser?.id ? { id: currentUser.id } : null,
      });
      setTitle(""); setCategory(""); setDescription(""); setScheduledDate("");
      setShowForm(false);
      await load();
    } catch (e) {
      alert("Tạo phiếu thất bại: " + e.message);
    } finally {
      setBusy(null);
    }
  };

  return (
    <Shell eyebrow="Kỹ thuật" title="Quản lý bảo trì">
      {summary && (
        <div style={{ display: "flex", gap: 12, marginBottom: 16, flexWrap: "wrap" }}>
          <StatBox label="Tổng số phiếu" value={summary.totalTickets} />
          <StatBox label="Chờ phân công" value={summary.byStatus?.PENDING ?? 0} />
          <StatBox label="Đang thực hiện" value={summary.byStatus?.IN_PROGRESS ?? 0} />
          <StatBox label="Hoàn thành" value={summary.byStatus?.DONE ?? 0} />
          <StatBox label="Tổng chi phí" value={fmtVnd(summary.totalCost)} wide />
        </div>
      )}

      <Card style={{ padding: 20, marginBottom: 16 }}>
        <PrimaryButton icon={Plus} onClick={() => setShowForm(!showForm)}>
          {showForm ? "Đóng" : "Tạo phiếu bảo trì"}
        </PrimaryButton>
        {showForm && (
          <div style={{ marginTop: 16 }}>
            <TextField label="Tiêu đề" value={title} onChange={(e) => setTitle(e.target.value)}
                       placeholder="Bảo trì định kỳ thang máy Tòa A" />
            <TextField label="Hạng mục" value={category} onChange={(e) => setCategory(e.target.value)}
                       placeholder="Thang máy, PCCC, Cấp thoát nước..." />
            <TextField label="Mô tả" value={description} onChange={(e) => setDescription(e.target.value)} />
            <TextField label="Ngày dự kiến" type="date" value={scheduledDate}
                       onChange={(e) => setScheduledDate(e.target.value)} />
            <PrimaryButton icon={Wrench} onClick={submitNew} disabled={busy === "new"}>
              {busy === "new" ? "Đang lưu..." : "Lưu phiếu"}
            </PrimaryButton>
          </div>
        )}
      </Card>

      {loading && <LoadingBlock />}
      {error && <ErrorBlock message={error} />}

      {tickets && (
        <Card style={{ padding: 8 }}>
          <table style={{ width: "100%", borderCollapse: "collapse" }}>
            <thead><tr>
              <Th style={{ paddingLeft: 16 }}>Phiếu</Th><Th>Hạng mục</Th><Th>Căn hộ</Th>
              <Th>Phụ trách</Th><Th>Ngày dự kiến</Th><Th>Chi phí</Th><Th>Trạng thái</Th><Th></Th>
            </tr></thead>
            <tbody>
              {tickets.map((t) => (
                <tr key={t.id} className="row-hover">
                  <Td style={{ paddingLeft: 16 }}>
                    <div style={{ fontWeight: 600 }}>{t.title}</div>
                    {t.incident && (
                      <div style={{ fontSize: 11.5, color: C.slate }}>
                        Từ phản ánh #{t.incident.id}
                      </div>
                    )}
                  </Td>
                  <Td>{t.category || "—"}</Td>
                  <Td><span className="f-mono">{t.apartment?.code || "Khu vực chung"}</span></Td>
                  <Td>{t.assignedTo?.fullName || <span style={{ color: C.slate }}>Chưa phân công</span>}</Td>
                  <Td className="f-mono">{t.scheduledDate || "—"}</Td>
                  <Td className="f-mono">{t.cost ? fmtVnd(t.cost) : "—"}</Td>
                  <Td><StatusBadge status={STATUS_LABEL[t.status] || t.status} /></Td>
                  <Td>
                    {t.status !== "DONE" && t.status !== "CANCELLED" && (
                      <div style={{ display: "flex", gap: 6, alignItems: "center" }}>
                        <select
                          defaultValue=""
                          disabled={busy === t.id}
                          onChange={(e) => doAssign(t, e.target.value)}
                          style={{ padding: "5px 7px", border: `1px solid ${C.line}`, borderRadius: 6, fontSize: 12 }}>
                          <option value="">Phân công...</option>
                          {technicians.map((u) => (
                            <option key={u.id} value={u.id}>{u.fullName}</option>
                          ))}
                        </select>
                        {t.status === "IN_PROGRESS" && (
                          <button onClick={() => doComplete(t)} disabled={busy === t.id}
                                  title="Hoàn thành phiếu"
                                  style={{
                                    display: "flex", alignItems: "center", gap: 4, cursor: "pointer",
                                    border: `1px solid ${C.green}`, color: C.green, background: C.greenSoft,
                                    borderRadius: 6, padding: "5px 9px", fontSize: 12,
                                  }}>
                            <CheckCircle2 size={13} /> Xong
                          </button>
                        )}
                      </div>
                    )}
                  </Td>
                </tr>
              ))}
            </tbody>
          </table>
        </Card>
      )}
    </Shell>
  );
}

function StatBox({ label, value, wide }) {
  return (
    <Card style={{ padding: "14px 18px", minWidth: wide ? 190 : 140 }}>
      <div style={{ fontSize: 12, color: C.slate, marginBottom: 4 }}>{label}</div>
      <div className="f-display" style={{ fontSize: 20, fontWeight: 700 }}>{value}</div>
    </Card>
  );
}
