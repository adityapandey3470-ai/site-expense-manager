import axios from "axios";

// Set VITE_API_BASE_URL in a .env file, e.g.
// VITE_API_BASE_URL=http://10.0.2.2:8080   (Android emulator -> localhost)
// VITE_API_BASE_URL=https://your-deployed-backend.com
const baseURL = import.meta.env.VITE_API_BASE_URL || "http://localhost:8080";

const api = axios.create({ baseURL });

api.interceptors.request.use((config) => {
  const token = localStorage.getItem("token");
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

api.interceptors.response.use(
    (response) => response,
    (error) => {
        const isLoginRequest = error.config?.url?.includes("/auth/login");

        if (error.response?.status === 401 && !isLoginRequest) {
            localStorage.removeItem("token");
            localStorage.removeItem("user");
            window.location.href = "/login";
        }
        return Promise.reject(error);
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
