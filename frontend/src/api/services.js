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

// --- Sự cố ---
export const getIncidents = () => api.get("/incidents");
export const createIncident = (incident) => api.post("/incidents", incident);

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
