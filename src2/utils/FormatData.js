// 날짜 포맷
export function fmtDate(dateStr) {
  if (!dateStr) return ''

  const date = new Date(dateStr)
  const yyyy = date.getFullYear()
  const mm = String(date.getMonth() + 1).padStart(2, '0')
  const dd = String(date.getDate()).padStart(2, '0')
  const hh = String(date.getHours()).padStart(2, '0')
  const min = String(date.getMinutes()).padStart(2, '0')

  return `${yyyy}-${mm}-${dd} ${hh}:${min}`
}

// 원화 포맷
export function fmtWon(n) {
  if (n == null) return '-'
  return n.toLocaleString()
}

// 아이디 마스킹
export function fmtId(id) {
  if (!id) return '-'
  if (id.length <= 2) return id[0] + '*'
  const first = id.slice(0, 2)
  const last = id.slice(-1)
  return `${first}***${last}`
}
