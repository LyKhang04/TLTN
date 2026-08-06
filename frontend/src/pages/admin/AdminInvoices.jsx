import React, { useState } from "react";
import { ChevronRight, Calculator } from "lucide-react";
import { C, fmtVnd } from "../../theme";
import { Shell, Card, Th, Td, StatusBadge, PrimaryButton, LoadingBlock, ErrorBlock, useApi } from "../../components/Common";
import { getInvoices, generateInvoices } from "../../api/services";

export default function AdminInvoices({ currentUser }) {
    const { data: invoices, loading, error, reload } = useApi(getInvoices);

    const now = new Date();
    const [month, setMonth] = useState(now.getMonth() + 1);
    const [year, setYear] = useState(now.getFullYear());
    const [running, setRunning] = useState(false);
    const [result, setResult] = useState(null);

    // Tu dong tinh hoa don cho toan bo can ho dang co nguoi o trong ky duoc chon
    const runGenerate = async () => {
        setRunning(true);
        setResult(null);
        try {
            const res = await generateInvoices(month, year, currentUser?.id);
            setResult(res);
            reload();
        } catch (e) {
            setResult({ message: "Lỗi: " + e.message });
        } finally {
            setRunning(false);
        }
    };

    return (
        <Shell eyebrow="Tài chính" title="Hóa đơn & thanh toán">
            <Card style={{ padding: 20, marginBottom: 16 }}>
                <div style={{ fontWeight: 700, marginBottom: 6 }}>Phát hành hóa đơn tự động</div>
                <div style={{ fontSize: 13, color: C.slate, marginBottom: 14 }}>
                    Hệ thống tính phí quản lý theo diện tích, tiền điện/nước theo chỉ số đã ghi
                    và phí gửi xe theo số phương tiện đăng ký. Căn hộ đã có hóa đơn của kỳ sẽ được bỏ qua.
                </div>
                <div style={{ display: "flex", gap: 10, alignItems: "center", flexWrap: "wrap" }}>
                    <label style={{ fontSize: 13 }}>Kỳ</label>
                    <select value={month} onChange={(e) => setMonth(Number(e.target.value))}
                            style={{ padding: "8px 10px", border: `1px solid ${C.line}`, borderRadius: 6 }}>
                        {Array.from({ length: 12 }, (_, i) => i + 1).map((m) => (
                            <option key={m} value={m}>Tháng {m}</option>
                        ))}
                    </select>
                    <input type="number" value={year} onChange={(e) => setYear(Number(e.target.value))}
                           style={{ width: 100, padding: "8px 10px", border: `1px solid ${C.line}`, borderRadius: 6 }} />
                    <PrimaryButton icon={Calculator} onClick={runGenerate} disabled={running}>
                        {running ? "Đang tính..." : "Phát hành hóa đơn"}
                    </PrimaryButton>
                </div>
                {result && (
                    <div style={{ marginTop: 14, padding: 12, background: C.mist, borderRadius: 8, fontSize: 13 }}>
                        <div style={{ fontWeight: 600 }}>{result.message}</div>
                        {result.createdApartments?.length > 0 && (
                            <div style={{ marginTop: 6 }}>
                                Đã tạo: <span className="f-mono">{result.createdApartments.join(", ")}</span>
                                {result.grandTotal != null && <> · Tổng tiền: <b>{fmtVnd(result.grandTotal)}</b></>}
                            </div>
                        )}
                        {result.skippedApartments?.length > 0 && (
                            <div style={{ marginTop: 4, color: C.slate }}>
                                Bỏ qua (đã có hóa đơn): <span className="f-mono">{result.skippedApartments.join(", ")}</span>
                            </div>
                        )}
                    </div>
                )}
            </Card>

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
