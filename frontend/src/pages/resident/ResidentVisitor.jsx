import React, { useState } from "react";
import { UserPlus } from "lucide-react";
import { C } from "../../theme";
import { Shell, Card, TextField, PrimaryButton, StatusBadge, LoadingBlock, ErrorBlock, useApi } from "../../components/Common";
import { getVisitorRegistrations, createVisitorRegistration } from "../../api/services";

export default function ResidentVisitor({ currentUser }) {
  const { data: visitors, loading, error, reload } = useApi(getVisitorRegistrations);
  const [form, setForm] = useState({ guestName: "", guestPhone: "", visitDate: "", expectedTime: "" });
  const [submitting, setSubmitting] = useState(false);

  const set = (k) => (e) => setForm((f) => ({ ...f, [k]: e.target.value }));

  const submit = async () => {
    setSubmitting(true);
    try {
      await createVisitorRegistration({
        resident: { id: currentUser?.id },
        guestName: form.guestName,
        guestPhone: form.guestPhone,
        visitDate: form.visitDate,
        expectedTime: form.expectedTime ? `${form.visitDate}T${form.expectedTime}:00` : null,
        status: "PENDING",
      });
      setForm({ guestName: "", guestPhone: "", visitDate: "", expectedTime: "" });
      reload();
    } catch (e) {
      alert("Đăng ký thất bại: " + e.message);
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <Shell eyebrow="An ninh" title="Đăng ký khách đến thăm">
      <div style={{ display: "flex", gap: 20, flexWrap: "wrap" }}>
        <Card style={{ padding: 24, flex: "1 1 380px" }}>
          <div className="f-display" style={{ fontWeight: 600, color: C.ink, fontSize: 15, marginBottom: 16 }}>Thông tin khách</div>
          <TextField label="Họ tên khách" value={form.guestName} onChange={set("guestName")} placeholder="Nguyễn Văn A" />
          <TextField label="Số điện thoại" value={form.guestPhone} onChange={set("guestPhone")} placeholder="090x xxx xxx" />
          <TextField label="Ngày đến thăm" type="date" value={form.visitDate} onChange={set("visitDate")} />
          <TextField label="Giờ dự kiến" type="time" value={form.expectedTime} onChange={set("expectedTime")} />
          <PrimaryButton icon={UserPlus} onClick={submit} disabled={submitting || !form.guestName || !form.visitDate}>
            {submitting ? "Đang gửi..." : "Đăng ký khách"}
          </PrimaryButton>
        </Card>
        <Card style={{ padding: 24, flex: "1 1 300px" }}>
          <div className="f-display" style={{ fontWeight: 600, color: C.ink, fontSize: 15, marginBottom: 16 }}>Lịch sử đăng ký</div>
          {loading && <LoadingBlock />}
          {error && <ErrorBlock message={error} />}
          {(visitors || []).slice(0, 6).map((v) => (
            <div key={v.id} style={{ display: "flex", justifyContent: "space-between", padding: "10px 0", borderBottom: `1px solid ${C.line}` }}>
              <div>
                <div style={{ fontSize: 13.5, color: C.ink }}>{v.guestName}</div>
                <div className="f-mono" style={{ fontSize: 11.5, color: C.slateLight }}>{v.visitDate}</div>
              </div>
              <StatusBadge status={v.status} />
            </div>
          ))}
        </Card>
      </div>
    </Shell>
  );
}
