export const C = {
  ink: "#1B2A4A",
  inkSoft: "#28395C",
  blue: "#2F5D8A",
  blueSoft: "#EAF1F8",
  amber: "#E0972F",
  amberSoft: "#FBF0DD",
  mist: "#F4F6F9",
  paper: "#FFFFFF",
  slate: "#64748B",
  slateLight: "#94A3B8",
  line: "#E4E8EF",
  green: "#3F9142",
  greenSoft: "#E9F5EA",
  red: "#D6484B",
  redSoft: "#FBEAEA",
};

export const STATUS_LABEL = {
  OCCUPIED: "Đang ở", VACANT: "Trống", MAINTENANCE: "Đang sửa chữa",
  UNPAID: "Chưa thanh toán", PAID: "Đã thanh toán", OVERDUE: "Quá hạn",
  NEW: "Mới", IN_PROGRESS: "Đang xử lý", RESOLVED: "Đã xử lý", REJECTED: "Từ chối",
  PENDING: "Chờ duyệt", APPROVED: "Đã duyệt", CHECKED_IN: "Đã vào", CHECKED_OUT: "Đã ra", CANCELLED: "Đã huỷ",
  CONFIRMED: "Đã xác nhận",
  ACTIVE: "Đang hoạt động", INACTIVE: "Ngưng hoạt động", LOCKED: "Bị khoá", LOST: "Báo mất",
};

export const STATUS_COLOR = {
  OCCUPIED: { bg: C.greenSoft, fg: C.green },
  VACANT: { bg: C.mist, fg: C.slate },
  MAINTENANCE: { bg: C.amberSoft, fg: C.amber },
  PAID: { bg: C.greenSoft, fg: C.green },
  UNPAID: { bg: C.amberSoft, fg: C.amber },
  OVERDUE: { bg: C.redSoft, fg: C.red },
  NEW: { bg: C.blueSoft, fg: C.blue },
  IN_PROGRESS: { bg: C.amberSoft, fg: C.amber },
  RESOLVED: { bg: C.greenSoft, fg: C.green },
  REJECTED: { bg: C.redSoft, fg: C.red },
  PENDING: { bg: C.amberSoft, fg: C.amber },
  APPROVED: { bg: C.greenSoft, fg: C.green },
  CONFIRMED: { bg: C.greenSoft, fg: C.green },
  CANCELLED: { bg: C.redSoft, fg: C.red },
  CHECKED_IN: { bg: C.greenSoft, fg: C.green },
  CHECKED_OUT: { bg: C.mist, fg: C.slate },
};

export const fmtVnd = (n) => (Number(n) || 0).toLocaleString("vi-VN") + "đ";
