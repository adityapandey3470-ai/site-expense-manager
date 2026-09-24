import axios from "axios";


const baseURL = import.meta.env.VITE_API_BASE_URL || "http://localhost:8080";

const api = axios.create({ baseURL });

api.interceptors.request.use((config) => {
  const token = localStorage.getItem("token");
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});


function forceLogout() {
    localStorage.removeItem("token");
    localStorage.removeItem("refreshToken");
    localStorage.removeItem("user");
    window.location.href = "/login";
}

let isRefreshing = false;
let pendingRequests = [];

function resolvePendingRequests(newToken) {
    pendingRequests.forEach((cb) => cb(newToken));
    pendingRequests = [];
}

api.interceptors.response.use(
    (response) => response,
    async (error) => {
        const originalRequest = error.config;
        const isAuthRoute =
            originalRequest?.url?.includes("/auth/login") ||
            originalRequest?.url?.includes("/auth/refresh");

        if (error.response?.status !== 401 || isAuthRoute || originalRequest._retry) {
            if (error.response?.status === 401 && isAuthRoute) {
                forceLogout();
            }
            return Promise.reject(error);
        }

        const refreshToken = localStorage.getItem("refreshToken");
        if (!refreshToken) {
            forceLogout();
            return Promise.reject(error);
        }

        originalRequest._retry = true;

        if (isRefreshing) {
            return new Promise((resolve) => {
                pendingRequests.push((newToken) => {
                    originalRequest.headers.Authorization = `Bearer ${newToken}`;
                    resolve(api(originalRequest));
                });
            });
        }

        isRefreshing = true;

        try {
            const res = await api.post("/auth/refresh", { refreshToken });
            const data = res.data;

            localStorage.setItem("token", data.token);
            localStorage.setItem("refreshToken", data.refreshToken);
            localStorage.setItem("user", JSON.stringify(data));

            resolvePendingRequests(data.token);
            originalRequest.headers.Authorization = `Bearer ${data.token}`;
            return api(originalRequest);
        } catch (refreshError) {
            resolvePendingRequests(null);
            forceLogout();
            return Promise.reject(refreshError);
        } finally {
            isRefreshing = false;
        }
    }
);

export async function uploadFile(file) {
    const formData = new FormData();
    formData.append("file", file);

    const res = await api.post("/files", formData, {
        headers: { "Content-Type": "multipart/form-data" },
    });

    return res.data.url;
}
export async function downloadReport(path, filename) {
    const res = await api.get(path, { responseType: "blob" });

    const url = window.URL.createObjectURL(new Blob([res.data]));
    const link = document.createElement("a");
    link.href = url;
    link.setAttribute("download", filename);
    document.body.appendChild(link);
    link.click();
    link.remove();
    window.URL.revokeObjectURL(url);
}

export function getErrorMessage(err) {
    const data = err?.response?.data;

    if (!data) return "Something went wrong. Please try again.";
    if (typeof data === "string") return data;

    if (data.message) return data.message;

    if (typeof data === "object") {
        const values = Object.values(data).filter((v) => typeof v === "string");
        if (values.length > 0) return values.join(", ");
    }

    return "Something went wrong. Please try again.";
}

export default api;
