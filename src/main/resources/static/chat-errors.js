function showChatError(message) {
    const box = document.getElementById('chat-box');
    if (box) {
        const line = document.createElement('div');
        line.style.color = '#b00020';
        line.textContent = `⚠ ${message}`;
        box.appendChild(line);
        box.scrollTop = box.scrollHeight;
    } else {
        console.error("⚠ Chat error:", message);
    }
}

async function handleChatApiError(response) {
    try {
        const contentType = response.headers.get('Content-Type') || '';
        let errorText;
        if (contentType.includes('application/json')) {
            const data = await response.json();
            errorText = data.error || data.message || JSON.stringify(data);
        } else {
            errorText = await response.text();
        }
        showChatError(errorText || `Unexpected error: ${response.status} ${response.statusText}`);
    } catch (e) {
        showChatError(`Error reading server response: ${e.message}`);
    }
}

async function fetchWithChatErrorHandling(url, options) {
    try {
        const res = await fetch(url, options);
        if (!res.ok) {
            await handleChatApiError(res);
            return null;
        }
        return res;
    } catch (err) {
        showChatError(`Network error: ${err.message}`);
        return null;
    }
}