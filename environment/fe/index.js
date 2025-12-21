/**
 * INSTANT CMS - SINGLE FILE LOGIC
 * Architecture: Vanilla JS imitating Component structure
 */

// ==========================================
// 1. STYLES (VS CODE THEME)
// ==========================================
const STYLES = `
:root {
  --bg-body: #020617;
  --bg-panel: #0f172a;
  --bg-input: #1e293b;
  --border-color: #334155;
  --text-main: #e2e8f0;
  --text-muted: #94a3b8;
  --primary: #3b82f6;
  --primary-hover: #2563eb;
  --danger: #ef4444;
  --success: #10b981;
}
* { box-sizing: border-box; }
body { font-family: 'Segoe UI', system-ui, sans-serif; color: var(--text-main); font-size: 14px; line-height: 1.5; overflow: hidden; }

/* LAYOUT */
.app-container { display: flex; height: 100vh; width: 100vw; }
.sidebar { width: 240px; background: var(--bg-panel); border-right: 1px solid var(--border-color); display: flex; flex-direction: column; flex-shrink: 0; }
.sidebar-header { padding: 20px; font-weight: 700; color: #60a5fa; font-size: 18px; border-bottom: 1px solid var(--border-color); }
.nav-item { padding: 12px 20px; cursor: pointer; color: var(--text-muted); transition: 0.2s; border-left: 3px solid transparent; }
.nav-item:hover { color: var(--text-main); background: rgba(255,255,255,0.02); }
.nav-item.active { background: rgba(59, 130, 246, 0.1); color: var(--primary); border-left-color: var(--primary); }

.main-content { flex: 1; display: flex; flex-direction: column; overflow: hidden; position: relative; }
.top-bar { height: 60px; border-bottom: 1px solid var(--border-color); display: flex; align-items: center; padding: 0 24px; gap: 16px; background: var(--bg-panel); flex-shrink: 0; }
.view-area { flex: 1; padding: 24px; overflow-y: auto; }

/* COMPONENTS */
.panel { background: var(--bg-panel); border: 1px solid var(--border-color); border-radius: 8px; padding: 20px; margin-bottom: 20px; }
.row { display: flex; gap: 12px; align-items: center; }
.col { display: flex; flex-direction: column; gap: 8px; }
.space-between { justify-content: space-between; }
.mt-4 { margin-top: 16px; }

input, select, textarea { width: 100%; padding: 8px 12px; background: var(--bg-input); border: 1px solid var(--border-color); border-radius: 6px; outline: none; color: var(--text-main); font-family: inherit; resize: vertical; }
input:focus, textarea:focus { border-color: var(--primary); }

button { cursor: pointer; padding: 8px 16px; border-radius: 6px; border: 1px solid var(--border-color); background: var(--bg-input); color: var(--text-main); transition: 0.2s; }
button:hover { background: var(--border-color); }
button:disabled { opacity: 0.5; cursor: not-allowed; }
button.primary { background: var(--primary); color: #fff; border-color: var(--primary); }
button.primary:hover { background: var(--primary-hover); }
button.danger { background-color: var(--danger); border-color: var(--danger); color: #fff; }
button.danger:hover { background-color: #dc2626; }


/* TABLE */
table { width: 100%; border-collapse: separate; border-spacing: 0; }
th { text-align: left; padding: 12px; background: rgba(2, 6, 23, 0.4); color: var(--text-muted); border-bottom: 1px solid var(--border-color); }
td { padding: 12px; border-bottom: 1px solid var(--border-color); }
tr:hover td { background: rgba(255,255,255,0.02); }

/* TERMINAL */
.terminal-window {
  border-top: 1px solid var(--border-color);
  background: #010409;
  flex-shrink: 0;
}
.terminal-header {
  background: var(--bg-panel);
  padding: 8px 12px;
  cursor: pointer;
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-family: monospace;
  font-size: 13px;
}
.terminal-header strong { color: #60a5fa; }
.terminal-header span {
  color: var(--text-muted);
  font-size: 11px;
}
.code-block {
  padding: 15px;
  height: 200px; 
  overflow-y: auto;
  font-family: monospace;
  font-size: 12px;
  white-space: pre; /* Use pre for better formatting of JSON */
  word-wrap: break-word;
  color: #c7d2fe;
}
.status-ok { color: #4ade80; }
.status-err { color: #ef4444; }

/* PAGINATION */
.pagination { display: flex; justify-content: flex-end; gap: 5px; margin-top: 16px; }
.pagination button { min-width: 32px; padding: 6px 10px; }

/* MODAL */
.modal-overlay { position: fixed; top:0; left:0; width:100%; height:100%; background:rgba(0,0,0,0.7); display:none; justify-content:center; align-items:center; z-index:100; backdrop-filter: blur(2px); }
.modal { background: var(--bg-panel); padding: 24px; border-radius: 8px; width: 400px; border: 1px solid var(--border-color); }
`;

// ==========================================
// 2. STATE MANAGEMENT
// ==========================================
const STATE = {
    activeTab: 'login', // login | user | circuitBreakers | loggers
    baseUrl: 'http://localhost:8080',
    accessToken: localStorage.getItem('cms_access_token') || '',
    refreshToken: localStorage.getItem('cms_refresh_token') || '',
    userQuery: { page: 1, size: 10, totalPages: 0, currentPage: 1 },
    logs: [],
    circuitBreakerInterval: null
};

// ==========================================
// 3. LOGIC & API
// ==========================================
function log(method, url, status, data) {
    const entry = { time: new Date().toLocaleTimeString(), method, url, status, data };
    STATE.logs.unshift(entry);
    if(STATE.logs.length > 100) STATE.logs.pop(); // Prevent memory leak
    renderTerminal();
}

/**
 * Performs a silent token refresh. Updates state and local storage.
 * @returns {Promise<boolean>} - True if refresh was successful, false otherwise.
 */
async function _internalRefreshToken() {
    if (!STATE.refreshToken) {
        console.log('No refresh token available for internal refresh.');
        return false;
    }

    const url = `${STATE.baseUrl}/api/v1/auth/refresh`;
    const headers = { 'Content-Type': 'application/json' };
    const body = JSON.stringify({ refreshToken: STATE.refreshToken });

    try {
        // Log the attempt
        const pendingLog = { refreshToken: '***' };
        log('POST', '/api/v1/auth/refresh', 'pending', pendingLog);
        
        const res = await fetch(url, { method: 'POST', headers, body });
        const text = await res.text();
        const data = text ? JSON.parse(text) : null;
        
        // Log the final result
        log('POST', '/api/v1/auth/refresh', res.status, data);

        if (res.ok && data && data.result && data.result.accessToken) {
            STATE.accessToken = data.result.accessToken;
            STATE.refreshToken = data.result.refreshToken || '';
            
            localStorage.setItem('cms_access_token', STATE.accessToken);
            if (STATE.refreshToken) {
                localStorage.setItem('cms_refresh_token', STATE.refreshToken);
            } else {
                localStorage.removeItem('cms_refresh_token');
            }
            console.log('Tokens silently refreshed.');
            renderApp();
            return true;
        } else {
            console.error('Internal refresh failed:', data?.message || res.statusText);
            return false;
        }
    } catch (e) {
        log('POST', '/api/v1/auth/refresh', 'ERR', e.message);
        console.error('Exception during internal refresh:', e);
        return false;
    }
}

/**
 * Silently clears local tokens and state, then re-renders the app.
 */
function _internalLogout() {
    console.log('Performing internal logout: clearing tokens.');
    STATE.accessToken = '';
    STATE.refreshToken = '';
    localStorage.removeItem('cms_access_token');
    localStorage.removeItem('cms_refresh_token');
    renderApp();
}

async function apiCall(endpoint, method = 'GET', body = null, useAuth = true, isRetry = false) {
    const url = `${STATE.baseUrl}${endpoint}`;
    const headers = { 'Content-Type': 'application/json' };
    if (useAuth && STATE.accessToken) {
        headers['Authorization'] = `Bearer ${STATE.accessToken}`;
    }
    
    try {
        const res = await fetch(url, { method, headers, body: body ? JSON.stringify(body) : null });

        // Intercept auth errors (401/403), ensure it's a protected route, and not already a retry.
        if ([401, 403].includes(res.status) && useAuth && !isRetry) {
            console.log(`API call to ${endpoint} failed with ${res.status}. Attempting token refresh.`);
            const refreshSuccess = await _internalRefreshToken();

            if (refreshSuccess) {
                console.log('Token refresh successful. Retrying original request.');
                return apiCall(endpoint, method, body, useAuth, true); // Retry request with new token
            } else {
                console.error('Token refresh failed. Logging out.');
                _internalLogout();
                
                // After logout, return the original error response to the caller.
                const errorData = { message: `Authentication Error (${res.status}) and token refresh failed. User logged out.` };
                log(method, endpoint, res.status, errorData);
                return { ok: false, data: errorData };
            }
        }
        
        const text = await res.text();
        // Handle cases where the response might be empty, like a 204 No Content
        if (!text) {
            log(method, endpoint, res.status, null);
            return { ok: res.ok, data: null };
        }
        const data = JSON.parse(text);
        log(method, endpoint, res.status, data);
        return { ok: res.ok, data };
    } catch (e) {
        log(method, endpoint, 'ERR', e.message);
        // Avoid showing an alert for every single API error, as it can be disruptive.
        // The error is logged to the terminal.
        console.error('API Error:', e.message); 
        return { ok: false, data: { message: e.message } };
    }
}


// ==========================================
// 4. COMPONENTS (HTML GENERATORS)
// ==========================================
const CssInjection = () => `<style>${STYLES}</style>`;

const Sidebar = () => `
    <aside class="sidebar">
        <div class="sidebar-header">SPRING ADMIN</div>
        <div class="nav-item ${STATE.activeTab === 'login' ? 'active' : ''}" onclick="switchTab('login')">
            Authentication
        </div>
        <div class="nav-item ${STATE.activeTab === 'user' ? 'active' : ''}" onclick="switchTab('user')">
            User Management
        </div>
        <div class="nav-item ${STATE.activeTab === 'circuitBreakers' ? 'active' : ''}" onclick="switchTab('circuitBreakers')">
            Circuit Breakers
        </div>
        <div class="nav-item ${STATE.activeTab === 'loggers' ? 'active' : ''}" onclick="switchTab('loggers')">
            Loggers
        </div>
    </aside>
`;

const TopBar = () => `
    <div class="top-bar">
        <div class="row">
            <span style="color:var(--text-muted); margin-right: 8px;">Base URL:</span>
            <input id="inpBaseUrl" value="${STATE.baseUrl}" onchange="STATE.baseUrl = this.value" style="width: 250px; font-family:monospace; color:#60a5fa; background: #020617; font-size: 12px; padding: 4px 8px;" />
        </div>
        <div style="flex:1"></div>
        <div class="row">
            <span style="font-size:12px; color: ${STATE.accessToken ? '#4ade80' : '#ef4444'}">
                ${STATE.accessToken ? '● Token Active' : '○ No Token'}
            </span>
        </div>
    </div>
`;

// --- VIEW: LOGIN ---
const LoginView = () => `
    <div id="view-login" class="view-area" style="display: ${STATE.activeTab === 'login' ? 'block' : 'none'}">
        <div class="panel" style="max-width: 900px; margin: 40px auto 0 auto;">
            <div class="row" style="align-items: flex-start; gap: 24px;">
                
                <div class="col" style="flex: 1; gap: 16px;">
                    <h3>Login</h3>
                    <div class="col">
                        <label style="font-size:12px; color:var(--text-muted)">Email</label>
                        <input id="inpEmail" placeholder="Email (e.g. system@admin.com)" value="system@admin.com" />
                    </div>
                    <div class="col">
                        <label style="font-size:12px; color:var(--text-muted)">Password</label>
                        <input id="inpPass" type="password" placeholder="Password" value="123456" />
                    </div>
                    <button class="primary" onclick="doLogin()">Login</button>
                </div>

                <div style="width: 1px; background: var(--border-color); align-self: stretch;"></div>

                <div class="col" style="flex: 2; gap: 16px;">
                    <h3>Token</h3>
                    <div class="col">
                        <label style="font-size:12px; color:var(--text-muted)">Access Token</label>
                        <textarea id="inpAccessToken" rows="6" style="font-family: monospace; font-size: 12px;">${STATE.accessToken}</textarea>
                    </div>
                    <div class="col">
                        <label style="font-size:12px; color:var(--text-muted)">Refresh Token (Optional)</label>
                        <textarea id="inpRefreshToken" rows="3" style="font-family: monospace; font-size: 12px;">${STATE.refreshToken}</textarea>
                    </div>
                    <div class="row space-between mt-4">
                        <button class="danger" onclick="doLogout()">Logout</button>
                        <div class="row">
                            <button onclick="doRefreshToken()">Refresh Token</button>
                            <button class="primary" onclick="saveTokens()" style="margin-left: 8px;">Update Manually</button>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
`;

// --- VIEW: USER ---
const UserView = () => `
    <div id="view-user" class="view-area" style="display: ${STATE.activeTab === 'user' ? 'block' : 'none'}">
        <div class="panel">
            <div class="row space-between">
                <h3>User List</h3>
                <div class="row">
                    <button style="border-color: #3b82f6; color: #3b82f6" onclick="openUserModal()">CREATE USER</button>
                    <button style="border-color: #facc15; color: #facc15" onclick="fetchUsers()">FETCH DATA</button>
                </div>
            </div>

            <div class="mt-4">
                <table id="tblUsers">
                    <thead>
                        <tr>
                            <th>Email</th>
                            <th>Status</th>
                            <th>User ID</th>
                            <th>Created At</th>
                        </tr>
                    </thead>
                    <tbody>
                        <tr><td colspan="4" style="text-align:center; color:var(--text-muted); padding:20px">Click FETCH to load data</td></tr>
                    </tbody>
                </table>
            </div>

            <div class="pagination" id="user-pagination">
                <!-- Pagination buttons will be rendered here -->
            </div>
        </div>
    </div>
`;

// --- VIEW: CIRCUIT BREAKERS ---
const CircuitBreakerView = () => `
    <div id="view-circuit-breakers" class="view-area" style="display: ${STATE.activeTab === 'circuitBreakers' ? 'block' : 'none'}">
        <div class="panel">
            <div class="row space-between">
                <h3>Circuit Breakers</h3>
                <button style="border-color: #facc15; color: #facc15" onclick="fetchCircuitBreakers()">FETCH DATA</button>
            </div>
            <div class="mt-4">
                <table id="tblCircuitBreakers">
                    <thead>
                        <tr>
                            <th>Name</th>
                            <th>State</th>
                            <th>Failure Rate</th>
                            <th>Slow Call Rate</th>
                            <th>Buffered</th>
                            <th>Failed</th>
                            <th>Slow</th>
                            <th>Not Permitted</th>
                            <th>Actions</th>
                        </tr>
                    </thead>
                    <tbody>
                        <tr><td colspan="9" style="text-align:center; color:var(--text-muted); padding:20px">Click FETCH to load data</td></tr>
                    </tbody>
                </table>
            </div>
        </div>
    </div>
`;

// --- VIEW: LOGGERS ---
const LoggerView = () => `
    <div id="view-loggers" class="view-area" style="display: ${STATE.activeTab === 'loggers' ? 'block' : 'none'}">
        <div class="panel">
            <div class="row space-between">
                <h3>Loggers</h3>
                <button style="border-color: #facc15; color: #facc15" onclick="fetchLoggers()">FETCH DATA</button>
            </div>
            <div class="mt-4">
                 <input id="logger-filter" onkeyup="filterLoggers()" placeholder="Filter loggers by name..." style="margin-bottom: 16px;"/>
                <table id="tblLoggers">
                    <thead>
                        <tr>
                            <th>Name</th>
                            <th>Level</th>
                            <th>Actions</th>
                        </tr>
                    </thead>
                    <tbody>
                        <tr><td colspan="3" style="text-align:center; color:var(--text-muted); padding:20px">Click FETCH to load data</td></tr>
                    </tbody>
                </table>
            </div>
        </div>
    </div>
`;


// --- MODAL: CREATE USER ---
const CreateUserModal = () => `
    <div id="modalUser" class="modal-overlay">
        <div class="modal">
            <h3>Create New User</h3>
            <div class="col mt-4">
                <input id="newEmail" placeholder="Email" />
                <input id="newPass" placeholder="Password" />
                <div class="row" style="margin-top:20px; justify-content:flex-end">
                    <button onclick="document.getElementById('modalUser').style.display='none'">Cancel</button>
                    <button class="primary" onclick="createUser()">Submit</button>
                </div>
            </div>
        </div>
    </div>
`;

const Terminal = () => `
  <div class="terminal-window">
    <div class="terminal-header" onclick="toggleRaw()">
      <strong>📟 TERMINAL / API OUTPUT</strong>
      <span>[ Click to Toggle ]</span>
    </div>
    <div id="rawRespContainer" style="display:block;">
       <div class="code-block" id="rawResp">// Ready. Waiting for requests...</div>
    </div>
  </div>
`;

// ==========================================
// 5. RENDER & ACTIONS
// ==========================================

function renderApp() {
    const root = document.getElementById('root');
    root.innerHTML = `
        ${CssInjection()}
        <div class="app-container">
            ${Sidebar()}
            <main class="main-content">
                ${TopBar()}
                ${LoginView()}
                ${UserView()}
                ${CircuitBreakerView()}
                ${LoggerView()}
                ${Terminal()}
            </main>
        </div>
        ${CreateUserModal()}
    `;
    renderTerminal();
    renderUserPagination();
}

function renderTerminal() {
    const rawResp = document.getElementById('rawResp');
    if (!rawResp) return;

    if (STATE.logs.length === 0) {
        rawResp.textContent = '// Ready. Waiting for requests...';
        return;
    }

    const lastLog = STATE.logs[0];
    let output = `[${lastLog.time}] ${lastLog.method} ${lastLog.url} - Status: ${lastLog.status}\n\n`;
    
    if (typeof lastLog.data === 'object' && lastLog.data !== null) {
        output += JSON.stringify(lastLog.data, null, 2);
    } else if (lastLog.data) {
        output += lastLog.data;
    } else {
        output += '// No response body.';
    }

    rawResp.textContent = output;
}

function renderUserPagination() {
    const pagContainer = document.getElementById('user-pagination');
    if (!pagContainer) return;

    let buttons = '';
    const total = STATE.userQuery.totalPages;
    const current = STATE.userQuery.currentPage;

    if (total <= 0) {
        pagContainer.innerHTML = '';
        return;
    }

    // Previous button
    buttons += `<button onclick="changePage(${current - 1})" ${current === 1 ? 'disabled' : ''}>&laquo;</button>`;

    // Page number buttons
    for (let i = 1; i <= total; i++) {
        buttons += `<button class="${i === current ? 'primary' : ''}" onclick="changePage(${i})">${i}</button>`;
    }
    
    // Next button
    buttons += `<button onclick="changePage(${current + 1})" ${current >= total ? 'disabled' : ''}>&raquo;</button>`;

    pagContainer.innerHTML = buttons;
}


// --- GLOBAL ACTIONS (Attached to Window for HTML onClick) ---

window.toggleRaw = () => {
    const container = document.getElementById('rawRespContainer');
    if (container) {
        container.style.display = container.style.display === 'none' ? 'block' : 'none';
    }
};

window.changePage = (page) => {
    if (page < 1 || page > STATE.userQuery.totalPages) return;
    STATE.userQuery.page = page;
    fetchUsers();
};

window.switchTab = (tab) => {
    if (STATE.circuitBreakerInterval) {
        clearInterval(STATE.circuitBreakerInterval);
        STATE.circuitBreakerInterval = null;
    }

    STATE.activeTab = tab;
    renderApp(); 
    if (tab === 'user') {
        fetchUsers();
    } else if (tab === 'circuitBreakers') {
        fetchCircuitBreakers();
        STATE.circuitBreakerInterval = setInterval(fetchCircuitBreakers, 5000); // Auto-refresh
    } else if (tab === 'loggers') {
        fetchLoggers();
    }
};

window.doLogout = async () => {
    // Call the backend logout endpoint. Pass 'true' for isRetry to prevent a refresh loop.
    if (STATE.accessToken) {
        await apiCall('/api/v1/auth/logout', 'POST', null, true, true);
    }
    _internalLogout(); // Clear local tokens
    alert('Logged out and local tokens cleared.');
};

window.saveTokens = () => {
    const accessToken = document.getElementById('inpAccessToken').value;
    const refreshToken = document.getElementById('inpRefreshToken').value;

    STATE.accessToken = accessToken;
    STATE.refreshToken = refreshToken;
    
    localStorage.setItem('cms_access_token', accessToken);
    localStorage.setItem('cms_refresh_token', refreshToken);

    alert('Tokens updated manually!');
    renderApp(); // Re-render to update top bar status
};

window.doLogin = async () => {
    const email = document.getElementById('inpEmail').value;
    const password = document.getElementById('inpPass').value;
    
    const res = await apiCall('/api/v1/auth/login', 'POST', { userEmail: email, userPassword: password }, false);
    
    if (res.ok && res.data.result && res.data.result.accessToken) {
        STATE.accessToken = res.data.result.accessToken;
        STATE.refreshToken = res.data.result.refreshToken || '';
        
        localStorage.setItem('cms_access_token', STATE.accessToken);
        if (STATE.refreshToken) {
            localStorage.setItem('cms_refresh_token', STATE.refreshToken);
        } else {
            localStorage.removeItem('cms_refresh_token');
        }

        alert('Login Success! Tokens saved.');
        renderApp(); // Re-render to show new token in text area and top bar
    } else {
        alert('Login Failed: ' + (res.data.message || 'Unknown error'));
    }
};

window.doRefreshToken = async () => {
    if (!STATE.refreshToken) {
        alert('No refresh token available.');
        return;
    }
    
    const success = await _internalRefreshToken();
    
    if (success) {
        alert('Tokens refreshed successfully!');
    } else {
        alert('Failed to refresh token. You will be logged out.');
        _internalLogout();
    }
};

window.fetchUsers = async () => {
    const page = STATE.userQuery.page;
    const size = STATE.userQuery.size;
    
    const res = await apiCall(`/api/v1/user/?page=${page}&size=${size}`, 'GET');
    
    const tbody = document.querySelector('#tblUsers tbody');
    if (res.ok && res.data.result) {
        const list = res.data.result.items || [];
        const meta = res.data.result.metaData;

        if (meta) {
            STATE.userQuery.totalPages = meta.totalPages;
            STATE.userQuery.currentPage = meta.currentPage;
        }

        tbody.innerHTML = '';
        
        list.forEach(u => {
            const tr = document.createElement('tr');
            tr.innerHTML = `
                <td style="color:#fff; font-weight:600">${u.userEmail}</td>
                <td><span style="padding:2px 8px; border-radius:10px; background:rgba(16, 185, 129, 0.2); color:#34d399; font-size:11px">${u.userStatus}</span></td>
                <td style="font-family:monospace; color:var(--text-muted)">${u.userId}</td>
                <td style="color:var(--text-muted)">${new Date(u.createdAt).toLocaleString()}</td>
            `;
            tbody.appendChild(tr);
        });
        
        if(list.length === 0) {
            tbody.innerHTML = '<tr><td colspan="4" style="text-align:center; padding:20px">No users found</td></tr>';
            STATE.userQuery.totalPages = 0;
        }
    } else {
        tbody.innerHTML = '<tr><td colspan="4" style="text-align:center; padding:20px; color:var(--danger)">Failed to load user data</td></tr>';
        STATE.userQuery.totalPages = 0;
    }
    renderUserPagination();
};

window.openUserModal = () => {
    document.getElementById('modalUser').style.display = 'flex';
};

window.createUser = async () => {
    const email = document.getElementById('newEmail').value;
    const password = document.getElementById('newPass').value;
    
    const res = await apiCall('/api/v1/user/', 'POST', { userEmail: email, userPassword: password });
    
    if(res.ok) {
        alert('User created!');
        document.getElementById('modalUser').style.display = 'none';
        document.getElementById('newEmail').value = '';
        document.getElementById('newPass').value = '';
        fetchUsers(); // Refresh the list
    }
};

// --- ACTUATOR ACTIONS ---

window.updateCircuitBreakerState = async (name, state) => {
    const res = await apiCall(`/actuator/circuitbreakers/${name}`, 'POST', { updateState: state }, false);

    if(res.ok) {
        // Force an immediate refresh
        fetchCircuitBreakers();
    } else {
        alert(`Failed to update circuit breaker '${name}'. See terminal for details.`);
    }
};

window.fetchCircuitBreakers = async () => {
    const tbody = document.querySelector('#tblCircuitBreakers tbody');
    // Do not show loading message on auto-refresh, it's distracting
    // tbody.innerHTML = '<tr><td colspan="8" style="text-align:center; padding:20px;">Loading...</td></tr>';
    
    const res = await apiCall('/actuator/circuitbreakers', 'GET', null, false);
    
    if (res.ok && res.data.circuitBreakers) {
        const cbs = res.data.circuitBreakers;
        tbody.innerHTML = '';

        const names = Object.keys(cbs);

        if (names.length === 0) {
            tbody.innerHTML = '<tr><td colspan="8" style="text-align:center; color:var(--text-muted); padding:20px">No circuit breakers found.</td></tr>';
            return;
        }

        names.forEach(name => {
            const cb = cbs[name];
            const stateColor = cb.state === 'CLOSED' ? 'var(--success)' : 'var(--danger)';
            const tr = document.createElement('tr');
            tr.innerHTML = `
                <td style="color:#fff; font-weight:600">${name}</td>
                <td><span style="font-weight:700; color:${stateColor}">${cb.state}</span></td>
                <td>${cb.failureRate}</td>
                <td>${cb.slowCallRate}</td>
                <td>${cb.bufferedCalls}</td>
                <td>${cb.failedCalls}</td>
                <td>${cb.slowCalls}</td>
                <td>${cb.notPermittedCalls}</td>
                <td>
                    <div class="row">
                        <button class="danger" style="padding: 4px 8px; font-size: 11px;" onclick="updateCircuitBreakerState('${name}', 'FORCE_OPEN')" ${cb.state === 'FORCE_OPEN' ? 'disabled' : ''}>Force Open</button>
                        <button class="primary" style="padding: 4px 8px; font-size: 11px;" onclick="updateCircuitBreakerState('${name}', 'CLOSE')" ${cb.state === 'CLOSED' ? 'disabled' : ''}>Closed</button>
                    </div>
                </td>
            `;
            tbody.appendChild(tr);
        });
    } else {
        // Only show error if the table is currently empty
        if (!tbody.hasChildNodes() || tbody.innerHTML.includes('Failed to load')) {
             tbody.innerHTML = '<tr><td colspan="8" style="text-align:center; color:var(--danger); padding:20px">Failed to load circuit breaker data</td></tr>';
        }
    }
};

window.fetchLoggers = async () => {
    const tbody = document.querySelector('#tblLoggers tbody');
    tbody.innerHTML = '<tr><td colspan="3" style="text-align:center; padding:20px;">Loading...</td></tr>';

    const res = await apiCall('/actuator/loggers', 'GET', null, false);

    if (res.ok && res.data.loggers) {
        const loggers = res.data.loggers;
        tbody.innerHTML = '';
        
        // Store the original full list of loggers for filtering
        window.allLoggers = loggers;
        
        // Render the initial list
        renderLoggerList(loggers);

    } else {
         tbody.innerHTML = '<tr><td colspan="3" style="text-align:center; color:var(--danger); padding:20px">Failed to load logger data</td></tr>';
    }
};

window.renderLoggerList = (loggers) => {
    const tbody = document.querySelector('#tblLoggers tbody');
    tbody.innerHTML = '';

    const allLevels = ["TRACE", "DEBUG", "INFO", "WARN", "ERROR", "FATAL", "OFF"];

    for (const name in loggers) {
        const logger = loggers[name];
        const effectiveLevel = logger.effectiveLevel;

        const levelOptions = allLevels.map(level => 
            `<option value="${level}" ${level === effectiveLevel ? 'selected' : ''}>${level}</option>`
        ).join('');

        const tr = document.createElement('tr');
        tr.setAttribute('data-logger-name', name); // For filtering
        tr.innerHTML = `
            <td style="color:#fff; font-weight:600; max-width: 400px; overflow: hidden; text-overflow: ellipsis; white-space: nowrap;" title="${name}">${name}</td>
            <td style="font-weight:bold; color: ${getLogLevelColor(effectiveLevel)}">${effectiveLevel}</td>
            <td>
                <div class="row">
                    <select id="select-level-${name}" style="width: 120px;">${levelOptions}</select>
                    <button class="primary" style="margin-left: 8px;" onclick="updateLoggerLevel('${name}')">Set</button>
                </div>
            </td>
        `;
        tbody.appendChild(tr);
    }
};

window.getLogLevelColor = (level) => {
    switch(level) {
        case 'TRACE': return '#a78bfa'; // purple-400
        case 'DEBUG': return '#60a5fa'; // blue-400
        case 'INFO': return '#22c55e';  // green-500
        case 'WARN': return '#facc15';  // yellow-400
        case 'ERROR': return '#ef4444'; // red-500
        case 'FATAL': return '#f43f5e'; // rose-500
        default: return 'var(--text-muted)';
    }
}

window.updateLoggerLevel = async (name) => {
    const select = document.getElementById(`select-level-${name}`);
    const level = select.value;

    const res = await apiCall(`/actuator/loggers/${name}`, 'POST', { configuredLevel: level }, false);

    if(res.ok) {
        // Optimistically update the UI, or re-fetch for confirmation
        fetchLoggers();
    } else {
        alert('Failed to update logger level. See terminal for details.');
    }
};

window.filterLoggers = () => {
    const filterText = document.getElementById('logger-filter').value.toLowerCase();
    const rows = document.querySelectorAll('#tblLoggers tbody tr');
    rows.forEach(row => {
        const name = row.getAttribute('data-logger-name').toLowerCase();
        if (name.includes(filterText)) {
            row.style.display = '';
        } else {
            row.style.display = 'none';
        }
    });
};


// INIT
renderApp();