
function checkMerchantPermission() {

    const token = localStorage.getItem('jwtToken');
    if (!token) {
        console.log('未找到token，重定向到登录页面');
        window.location.href = '/merchant/login.html';
        return false;
    }

    
    const userJson = localStorage.getItem('user');
    const user = userJson ? JSON.parse(userJson) : null;
    if (!user) {
        console.log('未找到用户信息，重定向到登录页面');
        window.location.href = '/merchant/login.html';
        return false;
    }


    if (!user.roles || !user.roles.includes('ROLE_MERCHANT')) {
        console.log('用户角色不包含ROLE_MERCHANT，显示无权限提示并跳转到首页');

        alert('您无权访问商家后台！只有商家可以进入。');

        window.location.href = '/index.html';
        return false;
    }

    console.log('权限验证通过，token和用户信息有效');
    return true;
}


function setupMerchantLogout() {
    const logoutButton = document.getElementById('logoutButton');
    if (logoutButton) {
        logoutButton.addEventListener('click', () => {
            
            localStorage.removeItem('jwtToken');
            localStorage.removeItem('user');
            
            window.location.href = '/merchant/login.html';
        });
    }
}

function displayMerchantName() {
    const userJson = localStorage.getItem('user');
    const user = userJson ? JSON.parse(userJson) : null;
    
    if (user) {
        const merchantNameElements = [
            document.getElementById('merchantName'),
            document.getElementById('sidebar-merchant-name')
        ];
        
        merchantNameElements.forEach(element => {
            if (element) {
                element.textContent = user.name || user.username || '商家名称';
            }
        });
        
        console.log('商家名称已更新:', user.name || user.username);
    }
}

function initMerchantPage(initCallback) {
    document.addEventListener('DOMContentLoaded', () => {
        console.log('DOM内容已加载，开始权限验证');
        
        // 在DOM加载完成后进行权限验证
        if (checkMerchantPermission()) {
            console.log('权限验证通过，设置登出功能并初始化页面');
            setupMerchantLogout();
            displayMerchantName(); // 显示商家名称
            if (typeof initCallback === 'function') {
                initCallback();
            }
        }
    });
}