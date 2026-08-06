import { getApartmentResidents } from "./services";

/**
 * Trả về danh sách id căn hộ mà cư dân đang đăng nhập được gán vào.
 * Dùng để lọc hóa đơn, sự cố, khách ghé thăm... theo đúng căn hộ của cư dân,
 * tránh việc cư dân này nhìn thấy dữ liệu của cư dân khác.
 */
export async function getMyApartmentIds(currentUser) {
  if (!currentUser?.id) return [];
  const links = await getApartmentResidents();
  return (links || [])
    .filter((l) => l.user?.id === currentUser.id)
    .map((l) => l.apartment?.id)
    .filter((id) => id != null);
}

/** Lọc danh sách bản ghi có trường `apartment` theo các căn hộ của cư dân. */
export function filterByApartments(items, apartmentIds) {
  const ids = new Set(apartmentIds);
  return (items || []).filter((it) => it.apartment?.id != null && ids.has(it.apartment.id));
}

/** Lọc danh sách bản ghi theo người tạo (reporter/resident) là cư dân hiện tại. */
export function filterByOwner(items, currentUser, field) {
  if (!currentUser?.id) return [];
  return (items || []).filter((it) => it[field]?.id === currentUser.id);
}
