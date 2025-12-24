
document.addEventListener('DOMContentLoaded', () => {

    
    
    
    const API_BASE_URL = 'http://localhost:8080/api';
    const token = localStorage.getItem('jwtToken');

    
    
    
    const authLinks = document.getElementById('auth-links');
    const loginLink = document.getElementById('login-link');
    const registerLink = document.getElementById('register-link');
    const userProfileLink = document.getElementById('user-profile-link');
    const logoutButton = document.getElementById('logout-button');

    const carouselIndicatorsContainer = document.getElementById('carousel-indicators-container');
    const carouselInnerContainer = document.getElementById('carousel-inner-container');
    const topRatedProductsContainer = document.getElementById('top-rated-products-container');
    const hotReviewsContainer = document.getElementById('hot-reviews-container');

    
    
    

    
    async function checkLoginStatus() {
        try {
            const token = localStorage.getItem('jwtToken');
            
            if (token) {
                console.log('检测到登录状态');
                if (loginLink) loginLink.classList.add('d-none');
                if (registerLink) registerLink.classList.add('d-none');
                if (userProfileLink) userProfileLink.classList.remove('d-none');

                const user = JSON.parse(localStorage.getItem('user'));
                if (user && (user.username || user.name) && userProfileLink) {
                    const profileLinkText = userProfileLink.querySelector('a');
                    if (profileLinkText) {
                        profileLinkText.innerHTML = `<i class="fa-solid fa-user"></i> ${user.name || user.username}`;
                    }
                }

                if (user && user.roles) {
                    const userRoles = user.roles || [];
                    console.log('从localStorage获取用户角色:', userRoles);
                    
                    showDashboardLinksBasedOnRoles(userRoles);
                } else {
                    console.log('localStorage中没有用户信息，尝试获取...');
                    
                    try {
                        await checkUserRoles();
                    } catch (error) {
                        console.warn('获取用户角色失败（不影响页面加载）:', error);
                    }
                }
            } else {
                console.log('未检测到登录状态');
                if (loginLink) loginLink.classList.remove('d-none');
                if (registerLink) registerLink.classList.remove('d-none');
                if (userProfileLink) userProfileLink.classList.add('d-none');

                hideDashboardLinks();
            }
        } catch (error) {
            console.error('检查登录状态时出错:', error);
        }
    }
    
    
    function showDashboardLinksBasedOnRoles(roles) {
        
        const adminLink = document.getElementById('admin-dashboard-link');
        const hasAdminRole = roles.some(role => 
            role === 'ADMIN' || role === 'ROLE_ADMIN'
        );
        
        if (adminLink && hasAdminRole) {
            console.log('用户有ADMIN角色，显示管理员链接');
            adminLink.classList.remove('d-none');
        }
        
        
        const merchantLink = document.getElementById('merchant-dashboard-link');
        const hasMerchantRole = roles.some(role => 
            role === 'MERCHANT' || role === 'ROLE_MERCHANT'
        );
        
        if (merchantLink && hasMerchantRole) {
            merchantLink.classList.remove('d-none');

            merchantLink.removeEventListener('click', handleMerchantLinkClick);

            merchantLink.addEventListener('click', handleMerchantLinkClick);
        }
    }

    function handleMerchantLinkClick(event) {
        event.preventDefault();

        const token = localStorage.getItem('jwtToken');
        const user = JSON.parse(localStorage.getItem('user'));
        
        if (token && user) {
            const isMerchant = user.roles && user.roles.includes('ROLE_MERCHANT');
            
            if (isMerchant) {
                window.location.href = '/merchant/dashboard.html';
            } else {
                alert('您不是商家账号，无法访问商家后台');
            }
        } else {
            window.location.href = '/merchant/login.html';
        }
    }
    
    
    async function checkUserRoles() {
        try {
            const token = localStorage.getItem('jwtToken');
            if (!token) {
                console.error('没有找到有效的token');
                return;
            }
            
            console.log('检查用户角色信息，token长度:', token.length);
            
            const response = await fetch(`${API_BASE_URL}/user/profile`, {
                method: 'GET',
                credentials: 'include', 
                headers: {
                    'Authorization': `Bearer ${token}`,
                    'Content-Type': 'application/json'
                }
            });
            
            console.log('API响应状态:', response.status);
            
            if (response.ok) {
                const userProfile = await response.json();
                console.log('用户角色信息:', userProfile);
                const roles = userProfile.roles || [];
                console.log('用户角色列表:', roles);

                const user = JSON.parse(localStorage.getItem('user'));
                if (user) {
                    user.roles = roles;
                    localStorage.setItem('user', JSON.stringify(user));
                }
                
                showDashboardLinksBasedOnRoles(roles);
            } else {
                console.error('获取用户角色失败，响应状态:', response.status);
                try {
                    const errorText = await response.text();
                    console.error('响应文本:', errorText);
                } catch (e) {
                    console.error('无法解析响应文本:', e);
                }
            }
        } catch (error) {
            console.error('获取用户角色信息失败:', error);
        }
    }
    
    
    function hideDashboardLinks() {
        const adminLink = document.getElementById('admin-dashboard-link');
        const merchantLink = document.getElementById('merchant-dashboard-link');
        
        if (adminLink) adminLink.classList.add('d-none');
        if (merchantLink) merchantLink.classList.add('d-none');
    }

    
    async function fetchData(endpoint) {
        try {
            const response = await fetch(`${API_BASE_URL}${endpoint}`);
            if (!response.ok) {
                throw new Error(`HTTP error! status: ${response.status}`);
            }
            return await response.json();
        } catch (error) {
            console.error(`Could not fetch data from ${endpoint}:`, error);
            
            return null;
        }
    }

    
    function renderCarousels(carousels) {
        if (!carouselInnerContainer || !carouselIndicatorsContainer) {
            
            return;
        }

        if (!carousels || carousels.length === 0) {
            const placeholderImage = window.placeholderGenerator ? 
                window.placeholderGenerator.generateBannerPlaceholder(1200, 400, '暂无横幅') : 
                'data:image/svg+xml;charset=UTF-8,%3Csvg%20width%3D%221200%22%20height%3D%22400%22%20xmlns%3D%22http%3A%2F%2Fwww.w3.org%2F2000%2Fsvg%22%3E%3Crect%20width%3D%22100%25%22%20height%3D%22100%25%22%20fill%3D%22%23F2F3F5%22%2F%3E%3Ctext%20x%3D%2250%25%22%20y%3D%2250%25%22%20font-family%3D%22Arial%2C%20sans-serif%22%20font-size%3D%2224%22%20fill%3D%22%234E5969%22%20text-anchor%3D%22middle%22%20dominant-baseline%3D%22middle%22%3E暂无横幅%3C%2Ftext%3E%3C%2Fsvg%3E';
            carouselInnerContainer.innerHTML = `<div class="carousel-item active"><img src="${placeholderImage}" class="d-block w-100" alt="暂无横幅"></div>`;
            return;
        }

        carouselIndicatorsContainer.innerHTML = '';
        carouselInnerContainer.innerHTML = '';

        carousels.forEach((carousel, index) => {
            const isActive = index === 0 ? 'active' : '';
            
            carouselIndicatorsContainer.innerHTML += `
                <button type="button" data-bs-target="#mainCarousel" data-bs-slide-to="${index}" class="${isActive}" aria-current="${isActive ? 'true' : 'false'}" aria-label="Slide ${index + 1}"></button>
            `;
            
            carouselInnerContainer.innerHTML += `
                <div class="carousel-item ${isActive}">
                    <a href="${carousel.targetUrl || '#'}">
                        <img src="${carousel.imageUrl}" class="d-block w-100" alt="Carousel Image ${index + 1}">
                    </a>
                </div>
            `;
        });
    }

    
    function renderProducts(products) {
        if (!topRatedProductsContainer) {
            
            return;
        }
        
        if (!products || products.length === 0) {
            topRatedProductsContainer.innerHTML = '<p class="text-center">暂无商品</p>';
            return;
        }

        topRatedProductsContainer.innerHTML = ''; 
        products.forEach(product => {
            topRatedProductsContainer.innerHTML += `
                <div class="col">
                    <div class="card h-100">
                        <img src="${product.imageUrls || (window.placeholderGenerator ? window.placeholderGenerator.generateProductPlaceholder(300, 200, product.name) : 'data:image/svg+xml;charset=UTF-8,%3Csvg%20width%3D%22300%22%20height%3D%22200%22%20xmlns%3D%22http%3A%2F%2Fwww.w3.org%2F2000%2Fsvg%22%3E%3Crect%20width%3D%22100%25%22%20height%3D%22100%25%22%20fill%3D%22%23165DFF%22%2F%3E%3Ctext%20x%3D%2250%25%22%20y%3D%2250%25%22%20font-family%3D%22Arial%2C%20sans-serif%22%20font-size%3D%2216%22%20fill%3D%22%23FFFFFF%22%20text-anchor%3D%22middle%22%20dominant-baseline%3D%22middle%22%3E商品图片%3C%2Ftext%3E%3C%2Fsvg%3E')}" class="card-img-top" alt="${product.name}">
                        <div class="card-body">
                            <h5 class="card-title">${product.name}</h5>
                            <p class="card-text">
                                <span class="rating-stars">${renderStars(product.averageRating)}</span>
                                <span class="text-muted small">(${product.averageRating.toFixed(1)} / ${product.reviewCount}条评价)</span>
                            </p>
                            <a href="/product/detail.html?id=${product.id}" class="btn btn-primary btn-sm">查看详情</a>
                        </div>
                    </div>
                </div>
            `;
        });
    }

    
    function renderReviews(reviews) {
        if (!hotReviewsContainer) {
            
            return;
        }
        
        if (!reviews || reviews.length === 0) {
            hotReviewsContainer.innerHTML = '<p class="text-center">暂无热门评价</p>';
            return;
        }

        hotReviewsContainer.innerHTML = ''; 
        reviews.forEach(review => {
            hotReviewsContainer.innerHTML += `
                 <a href="/product/detail.html?id=${review.productId}#review-${review.id}" class="list-group-item list-group-item-action review-item">
                    <div class="d-flex w-100 justify-content-between">
                        <h5 class="mb-1">${review.title}</h5>
                        <small class="review-rating">${renderStars(review.rating)}</small>
                    </div>
                    <p class="mb-1">${review.content.substring(0, 100)}...</p>
                    <small class="text-muted">由 ${review.username} 发布</small>
                </a>
            `;
        });
    }

    
    function renderStars(rating) {
        let stars = '';
        const fullStars = Math.floor(rating);
        const halfStar = rating % 1 >= 0.5;
        const emptyStars = 5 - fullStars - (halfStar ? 1 : 0);

        for (let i = 0; i < fullStars; i++) stars += '<i class="fa-solid fa-star"></i>';
        if (halfStar) stars += '<i class="fa-solid fa-star-half-stroke"></i>';
        for (let i = 0; i < emptyStars; i++) stars += '<i class="fa-regular fa-star"></i>';

        return stars;
    }

    
    function handleLogout() {
        localStorage.removeItem('jwtToken');
        localStorage.removeItem('user');

        window.location.reload(); 
    }

    
    
    if (logoutButton) {
        logoutButton.addEventListener('click', handleLogout);
    }

    
    
    

    
    async function initializePage() {
        try {
            
            await checkLoginStatus();

            
            const isHomePage = window.location.pathname === '/' || window.location.pathname === '/index.html';
            
            if (isHomePage) {
                
                const [carousels, productsPage, reviewsPage] = await Promise.all([
                    fetchData('/home/carousels'),
                    fetchData('/products?size=4&sort=averageRating,desc'), 
                    fetchData('/reviews/hot?size=5') 
                ]);
                
                if (carousels) renderCarousels(carousels);
                if (productsPage) renderProducts(productsPage.content);
                if (reviewsPage) renderReviews(reviewsPage.content);
            }
        } catch (error) {
            console.error('页面初始化错误:', error);
            
        }
    }
    
    
    initializePage();

});