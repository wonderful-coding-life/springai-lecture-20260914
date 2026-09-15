(() => {
  const $ = (selector) => document.querySelector(selector);
  const messages = $('#messages');
  const form = $('#chat-form');
  const input = $('#message-input');
  const username = $('#username');
  const sendButton = $('#send-button');
  const charCount = $('#char-count');
  let conversationId = crypto.randomUUID();
  let controller = null;

  const setBusy = (busy) => {
    // The same button becomes a stop control while the response is streaming.
    sendButton.disabled = false;
    sendButton.innerHTML = busy ? '<span class="send-icon" aria-hidden="true">■</span>' : '<span class="send-icon" aria-hidden="true">↑</span>';
    sendButton.setAttribute('aria-label', busy ? '응답 중단' : '메시지 전송');
  };
  const addMessage = (role, text = '') => {
    $('#welcome')?.remove();
    const row = document.createElement('div');
    row.className = `message-row ${role}`;
    const bubble = document.createElement('div');
    bubble.className = 'bubble';
    bubble.textContent = text;
    row.append(bubble);
    messages.append(row);
    messages.scrollTop = messages.scrollHeight;
    return bubble;
  };
  const appendAssistantText = (bubble, chunk) => {
    bubble.textContent += chunk;
    messages.scrollTop = messages.scrollHeight;
  };
  const parseSseLine = (line) => {
    if (!line.startsWith('data:')) return '';
    const value = line.slice(5).trim();
    if (!value || value === '[DONE]') return '';
    try { return JSON.parse(value); } catch { return value; }
  };
  async function streamReply(message, bubble) {
    controller = new AbortController();
    const params = new URLSearchParams({ conversationId, username: username.value.trim() || 'guest' });
    const response = await fetch(`/chats?${params}`, { method:'POST', headers:{'Content-Type':'text/plain;charset=UTF-8'}, body:message, signal:controller.signal });
    if (!response.ok || !response.body) throw new Error(`HTTP ${response.status}`);
    const reader = response.body.getReader();
    const decoder = new TextDecoder();
    let buffer = '';
    while (true) {
      const {value, done} = await reader.read();
      buffer += decoder.decode(value || new Uint8Array(), {stream:!done});
      const lines = buffer.split(/\r?\n/);
      buffer = lines.pop() || '';
      lines.forEach((line) => { const text = parseSseLine(line); if (text) appendAssistantText(bubble, text); });
      if (done) break;
    }
    if (!bubble.textContent) bubble.textContent = '응답을 받지 못했습니다. 잠시 후 다시 시도해 주세요.';
  }
  form.addEventListener('submit', async (event) => {
    event.preventDefault();
    const message = input.value.trim();
    if (!message || controller) return;
    input.value = ''; input.style.height = 'auto'; charCount.textContent = '0 / 4000';
    addMessage('user', message);
    const assistantRow = document.createElement('div'); assistantRow.className = 'message-row assistant streaming';
    const bubble = document.createElement('div'); bubble.className = 'bubble'; assistantRow.append(bubble); messages.append(assistantRow);
    setBusy(true);
    try { await streamReply(message, bubble); }
    catch (error) { if (error.name !== 'AbortError') bubble.textContent = '연결에 문제가 생겼습니다. 잠시 후 다시 시도해 주세요.'; }
    finally { assistantRow.classList.remove('streaming'); controller = null; setBusy(false); input.focus(); }
  });
  sendButton.addEventListener('click', () => { if (controller) controller.abort(); });
  input.addEventListener('input', () => { input.style.height = 'auto'; input.style.height = `${Math.min(input.scrollHeight,150)}px`; charCount.textContent = `${input.value.length} / 4000`; });
  input.addEventListener('keydown', (event) => { if (event.key === 'Enter' && !event.shiftKey) { event.preventDefault(); form.requestSubmit(); } });
  document.querySelectorAll('.suggestion').forEach((button) => button.addEventListener('click', () => { input.value = button.textContent; input.dispatchEvent(new Event('input')); input.focus(); }));
  $('#new-chat').addEventListener('click', () => { if (controller) controller.abort(); conversationId = crypto.randomUUID(); messages.innerHTML = '<div id="welcome" class="welcome"><div class="welcome-icon" aria-hidden="true">✦</div><p class="eyebrow">ORBIT ASSISTANT</p><h1>무엇을 도와드릴까요?</h1><p class="welcome-copy">궁금한 내용을 편하게 물어보세요.<br>대화의 맥락을 기억하고 이어서 답변해 드립니다.</p><div class="suggestions"><button type="button" class="suggestion">서비스 이용 방법을 알려줘</button><button type="button" class="suggestion">주문 상태를 확인하고 싶어</button><button type="button" class="suggestion">도움이 필요한 일이 있어</button></div></div>'; bindSuggestions(); input.focus(); });
  function bindSuggestions() { document.querySelectorAll('.suggestion').forEach((button) => button.addEventListener('click', () => { input.value = button.textContent; input.dispatchEvent(new Event('input')); input.focus(); })); }
})();
