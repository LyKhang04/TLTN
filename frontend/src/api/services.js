import { api } from "./client";

// --- Auth ---
export const login = (username, password) => api.post("/auth/login", { username, password });

// --- Dashboard ---
export const getDashboardSummary = () => api.get("/dashboard/summary");

// --- Bất động sản ---
export const getBuildings = () => api.get("/buildings");
export const getApartments = () => api.get("/apartments");

// --- Cư dân ---
export const getUsers = () => api.get("/users");
export const getApartmentResidents = () => api.get("/apartment-residents");

// --- Tài chính ---
export const getInvoices = () => api.get("/invoices");
export const getInvoiceItems = () => api.get("/invoice-items");
export const createPayment = (payment) => api.post("/payments", payment);
// Thanh toán theo nghiệp vụ: ghi nhận khoản trả VÀ cập nhật trạng thái hóa đơn
export const settleInvoice = (invoiceId, payment) =>
    api.post(`/payments/settle/${invoiceId}`, payment);
// Tu dong phat hanh hoa don cho toan bo can ho trong mot ky
export const generateInvoices = (month, year, issuedBy) =>
    api.post(`/invoices/generate?month=${month}&year=${year}` +
        (issuedBy ? `&issuedBy=${issuedBy}` : ""), {});

// --- Sự cố ---
export const getIncidents = () => api.get("/incidents");
export const createIncident = (incident) => api.post("/incidents", incident);
export const assignIncident = (id, payload) => api.post(`/incidents/${id}/assign`, payload);
export const updateIncidentStatus = (id, payload) => api.post(`/incidents/${id}/status`, payload);
export const getIncidentLogs = (id) => api.get(`/incidents/${id}/logs`);

// --- Tiện ích ---
export const getAmenities = () => api.get("/amenities");
export const getAmenityBookings = () => api.get("/amenity-bookings");
export const createAmenityBooking = (booking) => api.post("/amenity-bookings", booking);

// --- Khách ---
export const getVisitorRegistrations = () => api.get("/visitor-registrations");
export const createVisitorRegistration = (visitor) => api.post("/visitor-registrations", visitor);

// --- Thông báo ---
export const getNotifications = () => api.get("/notifications");
export const createNotification = (notification) => api.post("/notifications", notification);

// --- Tai khoan ---
export const registerAccount = (payload) => api.post("/auth/register", payload);
export const changePassword = (payload) => api.post("/auth/change-password", payload);

// --- Bao tri ---
export const getMaintenanceTickets = () => api.get("/maintenance-tickets");
export const createMaintenanceTicket = (t) => api.post("/maintenance-tickets", t);
export const assignMaintenance = (id, payload) => api.post(`/maintenance-tickets/${id}/assign`, payload);
export const completeMaintenance = (id, payload) => api.post(`/maintenance-tickets/${id}/complete`, payload);
export const getMaintenanceSummary = () => api.get("/maintenance-tickets/summary");
export const createTicketFromIncident = (incidentId, payload) =>
    api.post(`/maintenance-tickets/from-incident/${incidentId}`, payload);
