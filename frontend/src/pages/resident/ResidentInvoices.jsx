import React, { useEffect, useState } from "react";
import { CreditCard } from "lucide-react";
import { C, fmtVnd } from "../../theme";
import { Shell, Card, StatusBadge, PrimaryButton, LoadingBlock, ErrorBlock } from "../../components/Common";
import { getInvoices, settleInvoice } from "../../api/services";
import { getMyApartmentIds, filterByApartments } from "../../api/scope";

export default function ResidentInvoices({ currentUser }) {
  const [invoices, setInvoices] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [paying, setPaying] = useState(null);

  // Chỉ tải hóa đơn thuộc các căn hộ của chính cư dân đang đăng nhập.
  const load = async () => {
    setLoading(true);
    setError(null);
    try {
      const [all, myApartmentIds] = await Promise.all([
        getInvoices(),
        getMyApartmentIds(currentUser),
      ]);
      setInvoices(filterByApartments(all, myApartmentIds));
    } catch (e) {
      setError(e.message);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    load();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [currentUser?.id]);

  const pay = async (invoice) => {
    setPaying(invoice.id);
    try {
      // Gọi endpoint nghiệp vụ: vừa ghi nhận thanh toán vừa cập nhật trạng thái hóa đơn.
      await settleInvoice(invoice.id, {
        amount: invoice.totalAmount,
        method: "BANK_TRANSFER",
        status: "SUCCESS",
      });
      await load();
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
      {invoices && invoices.length === 0 && !loading && (
        <Card><div style={{ padding: 20, color: C.slate }}>Căn hộ của bạn hiện chưa có hóa đơn nào.</div></Card>
      )}
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
