import api from ".";

export async function fetchData(url, options = {}) {
  try {
    const { data } = await api.get(url, options);
    return data;
  } catch (error) {
    console.error("API 요청 실패:", error);
    throw error;
  }
}
