export function makeDateStr(year, month, day, type = "-") {
  return (
    year +
    type +
    (month < 10 ? "0" + month : month) +
    type +
    (day < 10 ? "0" + day : day)
  );
}

/**
 * Spring Boot LocalDateTime(KST) 문자열을 읽기 좋은 KST 날짜+시간 문자열로 변환.
 * 백엔드가 Asia/Seoul 타임존으로 저장하므로 문자열을 직접 파싱해
 * 브라우저 타임존 변환 없이 KST 그대로 표시한다.
 *
 * 입력 예) "2026-04-23T19:13:05.757"
 * 출력 예) "2026.04.23 19:13 (KST)"
 */
export function formatDateTime(dateStr, showKst = true) {
  if (!dateStr) return "-";

  // "2026-04-23T19:13:05.757" → datePart="2026-04-23", timePart="19:13:05.757"
  const [datePart, timePart] = dateStr.split("T");
  if (!datePart) return "-";

  const [year, month, day] = datePart.split("-");
  const time = timePart ? timePart.substring(0, 5) : "00:00";
  const formatted = `${year}.${month}.${day} ${time}`;

  return showKst ? `${formatted} (KST)` : formatted;
}

/** 날짜만 필요한 경우 (목록 등) */
export function formatDate(dateStr) {
  if (!dateStr) return "-";
  const [datePart] = dateStr.split("T");
  return datePart ? datePart.replace(/-/g, ".") : "-";
}
