export function createElement<K extends keyof HTMLElementTagNameMap>(
  tagName: K,
  content: string | Node | (string | Node)[],
  props: Record<string, string> = {},
  eventHandlers: Record<string, (ev: Event) => void> = {},
): HTMLElementTagNameMap[K] {
  const el = document.createElement(tagName);

  if (!Array.isArray(content)) {
    content = [content];
  }

  content.forEach(node => typeof node === 'string'
    ? el.innerHTML = el.innerHTML + node
    : el.appendChild(node),
  );

  Object.entries(props).forEach(([key, value]) => el.setAttribute(key, value));
  Object.entries(eventHandlers).forEach(([key, value]) => el.addEventListener(key, value));

  return el;
}
