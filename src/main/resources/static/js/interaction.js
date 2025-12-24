document.addEventListener('DOMContentLoaded', () => {
    
    
    
    const getToken = () => localStorage.getItem('jwtToken');
    
    
    const isUserLoggedIn = () => !!getToken();
    
    
    const showLoginPrompt = () => {
        alert('请先登录后再进行此操作');
        window.location.href = '/user/login.html';
    };
    
    
    const setupLikeButtons = () => {
        document.querySelectorAll('.like-btn').forEach(button => {
            button.addEventListener('click', async (e) => {
                e.preventDefault();
                e.stopPropagation();
                
                if (!isUserLoggedIn()) {
                    showLoginPrompt();
                    return;
                }
                
                const reviewId = button.dataset.reviewId;
                const isLiked = button.classList.contains('liked');
                const likeCountElement = document.getElementById(`like-count-${reviewId}`);
                let currentLikes = parseInt(likeCountElement.textContent);
                
                try {
                    const method = isLiked ? 'DELETE' : 'POST';
                    const response = await fetch(`/api/reviews/${reviewId}/like`, {
                        method: method,
                        headers: {
                            'Authorization': `Bearer ${getToken()}`,
                            'Content-Type': 'application/json'
                        }
                    });
                    
                    if (response.ok) {
                        
                        if (isLiked) {
                            button.classList.remove('liked', 'btn-danger');
                            button.classList.add('btn-outline-danger');
                            button.innerHTML = '<i class="far fa-thumbs-up"></i> 点赞';
                            likeCountElement.textContent = currentLikes - 1;
                        } else {
                            button.classList.add('liked', 'btn-danger');
                            button.classList.remove('btn-outline-danger');
                            button.innerHTML = '<i class="fas fa-thumbs-up"></i> 已点赞';
                            likeCountElement.textContent = currentLikes + 1;
                        }
                    } else {
                        throw new Error('操作失败');
                    }
                } catch (error) {
                    console.error('点赞操作失败:', error);
                    alert('点赞操作失败，请稍后再试');
                }
            });
        });
    };
    
    
    const setupFavoriteButtons = () => {
        document.querySelectorAll('.favorite-btn').forEach(button => {
            button.addEventListener('click', async (e) => {
                e.preventDefault();
                e.stopPropagation();
                
                if (!isUserLoggedIn()) {
                    showLoginPrompt();
                    return;
                }
                
                const reviewId = button.dataset.reviewId;
                const isFavorited = button.classList.contains('favorited');
                
                try {
                    const method = isFavorited ? 'DELETE' : 'POST';
                    const response = await fetch(`/api/reviews/${reviewId}/favorite`, {
                        method: method,
                        headers: {
                            'Authorization': `Bearer ${getToken()}`,
                            'Content-Type': 'application/json'
                        }
                    });
                    
                    if (response.ok) {
                        
                        if (isFavorited) {
                            button.classList.remove('favorited', 'btn-danger');
                            button.classList.add('btn-outline-danger');
                            button.innerHTML = '<i class="far fa-heart"></i> 收藏';
                        } else {
                            button.classList.add('favorited', 'btn-danger');
                            button.classList.remove('btn-outline-danger');
                            button.innerHTML = '<i class="fas fa-heart"></i> 已收藏';
                        }
                        
                        
                        if (window.location.pathname.includes('favorites.html')) {
                            if (isFavorited) {
                                button.closest('.list-group-item')?.remove();
                            }
                        }
                    } else {
                        throw new Error('操作失败');
                    }
                } catch (error) {
                    console.error('收藏操作失败:', error);
                    alert('收藏操作失败，请稍后再试');
                }
            });
        });
    };
    
    
    const setupShareButtons = () => {
        document.querySelectorAll('.share-btn').forEach(button => {
            button.addEventListener('click', (e) => {
                e.preventDefault();
                e.stopPropagation();
                
                const reviewId = button.dataset.reviewId;
                const shareUrl = `${window.location.origin}/product/detail.html?id=${button.dataset.productId}#review-${reviewId}`;
                
                
                navigator.clipboard.writeText(shareUrl)
                    .then(() => {
                        alert('分享链接已复制到剪贴板');
                    })
                    .catch(err => {
                        console.error('复制失败:', err);
                        alert('复制链接失败，请手动复制URL');
                    });
            });
        });
    };
    
    
    const initInteractionFeatures = () => {
        setupLikeButtons();
        setupFavoriteButtons();
        setupShareButtons();
    };
    
    
    initInteractionFeatures();
});