import React from "react";
import { Search, MoreHorizontal } from "lucide-react";
import { C } from "../../theme";
import { Shell, Card, Th, Td, StatusBadge, LoadingBlock, ErrorBlock, useApi } from "../../components/Common";
import { getApartments } from "../../api/services";

export default function AdminApartments() {
  const { data: apartments, loading, error } = useApi(getApartments);

  return (
    <Shell
      eyebrow="Bất động sản"
      title="Quản lý căn hộ"
      action={
        <div style={{ display: "flex", alignItems: "center", gap: 8, background: C.mist, border: `1px solid ${C.line}`, borderRadius: 10, padding: "9px 12px", width: 240 }}>
          <Search size={15} color={C.slateLight} />
          <span style={{ fontSize: 13, color: C.slateLight }}>Tìm theo mã căn hộ...</span>
        </div>
      }
    >
      {loading && <LoadingBlock />}
      {error && <ErrorBlock message={error} />}
      {apartments && (
        <Card style={{ padding: 8 }}>
          <table style={{ width: "100%", borderCollapse: "collapse" }}>
            <thead><tr>
              <Th style={{ paddingLeft: 16 }}>Mã căn hộ</Th><Th>Tòa nhà</Th><Th>Tầng</Th><Th>Diện tích</Th><Th>Trạng thái</Th><Th></Th>
            </tr></thead>
            <tbody>
              {apartments.map((a) => (
                <tr key={a.id} className="row-hover">
                  <Td style={{ paddingLeft: 16 }}><span className="f-mono" style={{ background: C.mist, padding: "3px 8px", borderRadius: 6, fontSize: 13, fontWeight: 600 }}>{a.code}</span></Td>
                  <Td>{a.building?.name}</Td>
                  <Td>{a.floor}</Td>
                  <Td>{a.area} m²</Td>
                  <Td><StatusBadge status={a.status} /></Td>
                  <Td><MoreHorizontal size={16} color={C.slateLight} /></Td>
                </tr>
              ))}
            </tbody>
          </table>
        </Card>
      )}
    </Shell>
  );
}
