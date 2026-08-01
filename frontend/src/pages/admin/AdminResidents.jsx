import React from "react";
import { MoreHorizontal } from "lucide-react";
import { C } from "../../theme";
import { Shell, Card, Th, Td, LoadingBlock, ErrorBlock, useApi } from "../../components/Common";
import { getApartmentResidents } from "../../api/services";

export default function AdminResidents() {
  const { data: residents, loading, error } = useApi(getApartmentResidents);

  return (
    <Shell eyebrow="Con người" title="Cư dân">
      {loading && <LoadingBlock />}
      {error && <ErrorBlock message={error} />}
      {residents && (
        <Card style={{ padding: 8 }}>
          <table style={{ width: "100%", borderCollapse: "collapse" }}>
            <thead><tr>
              <Th style={{ paddingLeft: 16 }}>Họ tên</Th><Th>Căn hộ</Th><Th>Số điện thoại</Th><Th>Quan hệ</Th><Th>Chủ hộ</Th><Th></Th>
            </tr></thead>
            <tbody>
              {residents.map((r) => (
                <tr key={r.id} className="row-hover">
                  <Td style={{ paddingLeft: 16, fontWeight: 500 }}>{r.user?.fullName}</Td>
                  <Td><span className="f-mono" style={{ background: C.mist, padding: "3px 8px", borderRadius: 6, fontSize: 13 }}>{r.apartment?.code}</span></Td>
                  <Td>{r.user?.phone}</Td>
                  <Td>{r.relationType}</Td>
                  <Td>{r.isPrimary ? "Có" : "Không"}</Td>
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
