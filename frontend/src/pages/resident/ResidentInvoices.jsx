import React, { useState } from "react";
import { CreditCard } from "lucide-react";
import { C, fmtVnd } from "../../theme";
import { Shell, Card, StatusBadge, PrimaryButton, LoadingBlock, ErrorBlock, useApi } from "../../components/Common";
import { getInvoices, createPayment } from "../../api/services";

export default function ResidentInvoices() {
  const { data: invoices, loading, error, reload } = useApi(getInvoices);
  const [paying, setPaying] = useState(null);

  const pay = async (invoice) => {
    setPaying(invoice.id);
    try {
      await createPayment({
        invoice: { id: invoice.id },
        amount: invoice.totalAmount,
        method: "BANK_TRANSFER",
        status: "SUCCESS",
      });
      reload();
    } catch (e) {
      alert("Thanh toán thất bại: " + e.message);
    } finally {
      setPaying(null);
    }
  };

  return (
    <Shell eyebrow="Tài chính cá nhân" title="Hóa đơn của tôi">
      {loading && <LoadingBlock />}
      {error && <ErrorBlock message={error} />}
      {invoices && invoices.map((inv) => (
        <Card key={inv.id} style={{ padding: 0, overflow: "hidden" }}>
          <div style={{ padding: 20, background: C.ink, color: "#fff", display: "flex", justifyContent: "space-between", alignItems: "center" }}>
            <div>
              <div className="f-mono" style={{ fontSize: 11, letterSpacing: 0.6, color: "#ffffffaa", textTransform: "uppercase" }}>
                Kỳ {inv.periodMonth}/{inv.periodYear} · Căn hộ {inv.apartment?.code}
              </div>
              <div className="f-display" style={{ fontSize: 22, fontWeight: 700, marginTop: 4 }}>{fmtVnd(inv.totalAmount)}</div>
            </div>
            <StatusBadge status={inv.status} />
          </div>
          {inv.status !== "PAID" && (
            <div style={{ padding: 20, borderTop: `1px solid ${C.line}` }}>
              <PrimaryButton icon={CreditCard} onClick={() => pay(inv)} disabled={paying === inv.id}>
                {paying === inv.id ? "Đang xử lý..." : "Thanh toán ngay"}
              </PrimaryButton>
            </div>
          )}
        </Card>
      ))}
    </Shell>
  );
}
