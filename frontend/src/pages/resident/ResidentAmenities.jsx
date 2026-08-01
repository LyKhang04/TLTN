import React, { useState } from "react";
import { C } from "../../theme";
import { Shell, Card, LoadingBlock, ErrorBlock, useApi } from "../../components/Common";
import { getAmenities, createAmenityBooking } from "../../api/services";

export default function ResidentAmenities({ currentUser }) {
  const { data: amenities, loading, error } = useApi(getAmenities);
  const [booking, setBooking] = useState(null);

  const book = async (amenity) => {
    setBooking(amenity.id);
    try {
      await createAmenityBooking({
        amenity: { id: amenity.id },
        resident: { id: currentUser?.id },
        bookingDate: new Date().toISOString().slice(0, 10),
        status: "PENDING",
      });
      alert(`Đã gửi yêu cầu đặt ${amenity.name}`);
    } catch (e) {
      alert("Đặt lịch thất bại: " + e.message);
    } finally {
      setBooking(null);
    }
  };

  return (
    <Shell eyebrow="Tiện ích nội khu" title="Đặt tiện ích">
      {loading && <LoadingBlock />}
      {error && <ErrorBlock message={error} />}
      <div style={{ display: "flex", gap: 16, flexWrap: "wrap" }}>
        {(amenities || []).map((a) => (
          <Card key={a.id} style={{ padding: 20, flex: "1 1 280px", minWidth: 260 }}>
            <div className="f-display" style={{ fontWeight: 600, color: C.ink, fontSize: 14.5, marginBottom: 6 }}>{a.name}</div>
            <div style={{ fontSize: 12.5, color: C.slate, marginBottom: 14 }}>
              Sức chứa {a.capacity} người · {a.openTime} – {a.closeTime}
            </div>
            <button
              onClick={() => book(a)}
              disabled={booking === a.id}
              className="f-body"
              style={{
                width: "100%", background: "transparent", border: `1px solid ${C.ink}`, color: C.ink,
                borderRadius: 8, padding: "9px 0", fontSize: 13.5, fontWeight: 600, cursor: "pointer",
              }}
            >
              {booking === a.id ? "Đang gửi..." : "Đặt lịch"}
            </button>
          </Card>
        ))}
      </div>
    </Shell>
  );
}
