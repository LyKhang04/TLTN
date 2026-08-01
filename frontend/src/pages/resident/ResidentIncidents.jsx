import React, { useState } from "react";
import { Wrench, Plus } from "lucide-react";
import { C } from "../../theme";
import { Shell, Card, StatusBadge, PrimaryButton, TextField, LoadingBlock, ErrorBlock, useApi } from "../../components/Common";
import { getIncidents, createIncident } from "../../api/services";

export default function ResidentIncidents({ currentUser }) {
  const { data: incidents, loading, error, reload } = useApi(getIncidents);
  const [showForm, setShowForm] = useState(false);
  const [category, setCategory] = useState("");
  const [description, setDescription] = useState("");
  const [submitting, setSubmitting] = useState(false);

  const submit = async () => {
    setSubmitting(true);
    try {
      await createIncident({
        reporter: { id: currentUser?.id },
        category,
        description,
        status: "NEW",
      });
      setCategory(""); setDescription(""); setShowForm(false);
      reload();
    } catch (e) {
      alert("Gửi thất bại: " + e.message);
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <Shell
      eyebrow="Hỗ trợ"
      title="Báo sự cố"
      action={<PrimaryButton icon={Plus} onClick={() => setShowForm((s) => !s)}>Báo sự cố mới</PrimaryButton>}
    >
      {showForm && (
        <Card style={{ padding: 20 }}>
          <TextField label="Loại sự cố" value={category} onChange={(e) => setCategory(e.target.value)} placeholder="Điện nước, thang máy..." />
          <TextField label="Mô tả" value={description} onChange={(e) => setDescription(e.target.value)} />
          <PrimaryButton onClick={submit} disabled={submitting || !description}>Gửi báo cáo</PrimaryButton>
        </Card>
      )}

      {loading && <LoadingBlock />}
      {error && <ErrorBlock message={error} />}
      <div style={{ display: "flex", flexDirection: "column", gap: 12 }}>
        {(incidents || []).map((i) => (
          <Card key={i.id} style={{ padding: 18, display: "flex", justifyContent: "space-between", alignItems: "center" }}>
            <div style={{ display: "flex", gap: 14, alignItems: "flex-start" }}>
              <div style={{ width: 36, height: 36, borderRadius: 9, background: C.blueSoft, display: "flex", alignItems: "center", justifyContent: "center", flexShrink: 0 }}>
                <Wrench size={16} color={C.blue} />
              </div>
              <div>
                <div style={{ fontSize: 14, fontWeight: 500, color: C.ink }}>{i.description}</div>
                <div className="f-mono" style={{ fontSize: 11.5, color: C.slateLight, marginTop: 2 }}>{i.category}</div>
              </div>
            </div>
            <StatusBadge status={i.status} />
          </Card>
        ))}
      </div>
    </Shell>
  );
}
