import React from "react";
import { ChevronRight } from "lucide-react";
import { C, fmtVnd } from "../../theme";
import { Shell, Card, Th, Td, StatusBadge, LoadingBlock, ErrorBlock, useApi } from "../../components/Common";
import { getInvoices } from "../../api/services";

export default function AdminInvoices() {
  const { data: invoices, loading, error } = useApi(getInvoices);

  return (
    <Shell eyebrow="Tài chính" title="Hóa đơn & thanh toán">
      {loading && <LoadingBlock />}
      {error && <ErrorBlock message={error} />}
      {invoices && (
        <Card style={{ padding: 8 }}>
          <table style={{ width: "100%", borderCollapse: "collapse" }}>
            <thead><tr>
              <Th style={{ paddingLeft: 16 }}>Căn hộ</Th><Th>Kỳ</Th><Th>Số tiền</Th><Th>Trạng thái</Th><Th></Th>
            </tr></thead>
            <tbody>
              {invoices.map((i) => (
                <tr key={i.id} className="row-hover">
                  <Td style={{ paddingLeft: 16 }}><span className="f-mono">{i.apartment?.code}</span></Td>
                  <Td>{i.periodMonth}/{i.periodYear}</Td>
                  <Td className="f-mono" style={{ fontWeight: 600 }}>{fmtVnd(i.totalAmount)}</Td>
                  <Td><StatusBadge status={i.status} /></Td>
                  <Td><ChevronRight size={16} color={C.slateLight} /></Td>
                </tr>
              ))}
            </tbody>
          </table>
        </Card>
      )}
    </Shell>
  );
}
