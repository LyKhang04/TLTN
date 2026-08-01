import React, { useState } from "react";
import { Plus } from "lucide-react";
import { C } from "../../theme";
import { Shell, Card, PrimaryButton, TextField, LoadingBlock, ErrorBlock, useApi } from "../../components/Common";
import { getNotifications, createNotification } from "../../api/services";

export default function AdminNotifications() {
  const { data: notifications, loading, error, reload } = useApi(getNotifications);
  const [showForm, setShowForm] = useState(false);
  const [title, setTitle] = useState("");
  const [content, setContent] = useState("");
  const [submitting, setSubmitting] = useState(false);

  const submit = async () => {
    setSubmitting(true);
    try {
      await createNotification({ title, content, targetScope: "ALL", createdBy: { id: 1 } });
      setTitle(""); setContent(""); setShowForm(false);
      reload();
    } catch (e) {
      alert("Gửi thất bại: " + e.message);
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <Shell
      eyebrow="Truyền thông"
      title="Thông báo"
      action={<PrimaryButton icon={Plus} onClick={() => setShowForm((s) => !s)}>Soạn thông báo</PrimaryButton>}
    >
      {showForm && (
        <Card style={{ padding: 20 }}>
          <TextField label="Tiêu đề" value={title} onChange={(e) => setTitle(e.target.value)} />
          <TextField label="Nội dung" value={content} onChange={(e) => setContent(e.target.value)} />
          <PrimaryButton onClick={submit} disabled={submitting || !title}>Gửi cho toàn khu</PrimaryButton>
        </Card>
      )}

      {loading && <LoadingBlock />}
      {error && <ErrorBlock message={error} />}
      {notifications && (
        <div style={{ display: "flex", flexDirection: "column", gap: 12 }}>
          {notifications.map((n) => (
            <Card key={n.id} style={{ padding: 18 }}>
              <div style={{ fontSize: 14.5, fontWeight: 600, color: C.ink, marginBottom: 4 }}>{n.title}</div>
              <div style={{ fontSize: 13, color: C.slate, lineHeight: 1.5 }}>{n.content}</div>
            </Card>
          ))}
        </div>
      )}
    </Shell>
  );
}
