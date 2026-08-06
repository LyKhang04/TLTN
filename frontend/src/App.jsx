import React, { useMemo, useState } from "react";
import {
    LayoutDashboard, Building2, Users, Receipt, Wrench, Bell, Home,
    UserPlus, CalendarClock, LogOut, KeyRound, HardHat, Car, SlidersHorizontal } from "lucide-react";
import { C } from "./theme";
import Login from "./pages/Login";
import ChatWidget from "./components/ChatWidget";

import AdminDashboard from "./pages/admin/AdminDashboard";
import AdminApartments from "./pages/admin/AdminApartments";
import AdminResidents from "./pages/admin/AdminResidents";
import AdminInvoices from "./pages/admin/AdminInvoices";
import AdminIncidents from "./pages/admin/AdminIncidents";
import AdminNotifications from "./pages/admin/AdminNotifications";
import AdminMaintenance from "./pages/admin/AdminMaintenance";
import AdminOperations from "./pages/admin/AdminOperations";
import AdminVehicles from "./pages/admin/AdminVehicles";

import ResidentHome from "./pages/resident/ResidentHome";
import ResidentInvoices from "./pages/resident/ResidentInvoices";
import ResidentVisitor from "./pages/resident/ResidentVisitor";
import ResidentIncidents from "./pages/resident/ResidentIncidents";
import ResidentAmenities from "./pages/resident/ResidentAmenities";
import ResidentNotifications from "./pages/resident/ResidentNotifications";
import ResidentAccount from "./pages/resident/ResidentAccount";

function FloorMark({ size = 22 }) {
    return (
        <div style={{ width: size, height: size, display: "grid", gridTemplateColumns: "1fr 1fr", gap: 2 }}>
            {[0, 1, 2, 3].map((i) => (
                <div key={i} style={{ borderRadius: 2, background: i === 2 ? C.amber : "rgba(255,255,255,0.28)" }} />
            ))}
        </div>
    );
}

const ADMIN_NAV = [
    { key: "dashboard", label: "Tổng quan", icon: LayoutDashboard, view: AdminDashboard },
    { key: "apartments", label: "Căn hộ", icon: Building2, view: AdminApartments },
    { key: "residents", label: "Cư dân", icon: Users, view: AdminResidents },
    { key: "invoices", label: "Hóa đơn & thanh toán", icon: Receipt, view: AdminInvoices },
    { key: "incidents", label: "Sự cố", icon: Wrench, view: AdminIncidents },
    { key: "maintenance", label: "Bảo trì", icon: HardHat, view: AdminMaintenance },
    { key: "vehicles", label: "Phương tiện & Thẻ", icon: Car, view: AdminVehicles },
    { key: "operations", label: "Vận hành & Cấu hình", icon: SlidersHorizontal, view: AdminOperations },
    { key: "notifications", label: "Thông báo", icon: Bell, view: AdminNotifications },
];

const RESIDENT_NAV = [
    { key: "home", label: "Trang chủ", icon: Home, view: ResidentHome },
    { key: "myinvoices", label: "Hóa đơn của tôi", icon: Receipt, view: ResidentInvoices },
    { key: "visitor", label: "Đăng ký khách", icon: UserPlus, view: ResidentVisitor },
    { key: "incidents", label: "Báo sự cố", icon: Wrench, view: ResidentIncidents },
    { key: "amenities", label: "Đặt tiện ích", icon: CalendarClock, view: ResidentAmenities },
    { key: "notifications", label: "Thông báo", icon: Bell, view: ResidentNotifications },
    { key: "account", label: "Tài khoản", icon: KeyRound, view: ResidentAccount },
];

export default function App() {
    const [currentUser, setCurrentUser] = useState(null);

    const isAdmin = currentUser?.roleName === "ADMIN";
    const nav = isAdmin ? ADMIN_NAV : RESIDENT_NAV;
    const [active, setActive] = useState(nav[0]?.key);

    const current = useMemo(() => nav.find((n) => n.key === active) || nav[0], [nav, active]);

    if (!currentUser) {
        return <Login onSuccess={(u) => { setCurrentUser(u); setActive((u.roleName === "ADMIN" ? ADMIN_NAV : RESIDENT_NAV)[0].key); }} />;
    }

    const ActiveView = current.view;

    return (
        <div className="f-body" style={{ display: "flex", height: "100vh", background: C.mist }}>
            {/* Sidebar */}
            <div style={{ width: 240, background: C.ink, display: "flex", flexDirection: "column", flexShrink: 0 }}>
                <div style={{ padding: "22px 20px 18px", display: "flex", alignItems: "center", gap: 10 }}>
                    <FloorMark size={22} />
                    <div>
                        <div className="f-display" style={{ color: "#fff", fontWeight: 700, fontSize: 16, lineHeight: 1 }}>Sảnh</div>
                        <div className="f-mono" style={{ color: "#ffffff77", fontSize: 10, marginTop: 3 }}>NỀN TẢNG QUẢN LÝ CHUNG CƯ</div>
                    </div>
                </div>

                <div style={{ padding: "0 12px", display: "flex", flexDirection: "column", gap: 2, flex: 1 }}>
                    {nav.map((item) => {
                        const Icon = item.icon;
                        const isActive = item.key === active;
                        return (
                            <button
                                key={item.key}
                                onClick={() => setActive(item.key)}
                                className="nav-item f-body"
                                style={{
                                    display: "flex", alignItems: "center", gap: 10, padding: "10px 12px", borderRadius: 9,
                                    border: "none", cursor: "pointer", textAlign: "left",
                                    background: isActive ? "#ffffff14" : "transparent",
                                    color: isActive ? "#fff" : "#ffffffa0",
                                    fontSize: 13.5, fontWeight: isActive ? 600 : 500,
                                }}
                            >
                                <Icon size={16} />
                                {item.label}
                            </button>
                        );
                    })}
                </div>

                <div style={{ margin: 16, padding: 14, background: "#ffffff0d", borderRadius: 10, display: "flex", gap: 10, alignItems: "center" }}>
                    <div style={{
                        width: 32, height: 32, borderRadius: 999, background: C.amber, display: "flex",
                        alignItems: "center", justifyContent: "center", color: C.ink, fontWeight: 700, fontSize: 13, flexShrink: 0,
                    }}>
                        {(currentUser.fullName || "?").charAt(0)}
                    </div>
                    <div style={{ minWidth: 0, flex: 1 }}>
                        <div style={{ color: "#fff", fontSize: 12.5, fontWeight: 600, whiteSpace: "nowrap", overflow: "hidden", textOverflow: "ellipsis" }}>
                            {currentUser.fullName}
                        </div>
                        <div className="f-mono" style={{ color: "#ffffff77", fontSize: 10.5 }}>{currentUser.username}</div>
                    </div>
                    <button
                        onClick={() => setCurrentUser(null)}
                        title="Đăng xuất"
                        style={{ background: "transparent", border: "none", cursor: "pointer", color: "#ffffffa0" }}
                    >
                        <LogOut size={16} />
                    </button>
                </div>
            </div>

            <ActiveView currentUser={currentUser} />

            {!isAdmin && <ChatWidget currentUser={currentUser} />}
        </div>
    );
}
