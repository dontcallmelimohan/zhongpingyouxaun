


class PlaceholderGenerator {
    constructor() {
        this.baseColors = {
            primary: '#165DFF',
            success: '#00B42A',
            warning: '#FF7D00',
            danger: '#F53F3F',
            info: '#165DFF',
            gray: '#86909C'
        };
    }

    
    generateSVG(width, height, text = '', bgColor = '#F2F3F5', textColor = '#4E5969') {
        const encodedText = encodeURIComponent(text);
        const fontSize = Math.min(width / 10, height / 5, 24);
        
        return `data:image/svg+xml;charset=UTF-8,${encodeURIComponent(`
            <svg width="${width}" height="${height}" xmlns="http://www.w3.org/2000/svg">
                <rect width="100%" height="100%" fill="${bgColor}"/>
                <text x="50%" y="50%" 
                      font-family="Arial, sans-serif" 
                      font-size="${fontSize}" 
                      fill="${textColor}" 
                      text-anchor="middle" 
                      dominant-baseline="middle">
                    ${text}
                </text>
            </svg>
        `)}`;
    }

    
    generateProductPlaceholder(width, height, productName = '') {
        const colors = ['#165DFF', '#00B42A', '#FF7D00', '#F53F3F', '#722ED1'];
        const bgColor = colors[Math.floor(Math.random() * colors.length)];
        const textColor = '#FFFFFF';
        
        return this.generateSVG(width, height, productName || '商品图片', bgColor, textColor);
    }

    
    generateBannerPlaceholder(width, height, text = '') {
        return this.generateSVG(width, height, text || '加载中...', '#165DFF', '#FFFFFF');
    }

    
    generateGenericPlaceholder(width, height, text = '') {
        return this.generateSVG(width, height, text || '图片', '#F2F3F5', '#4E5969');
    }
}


window.placeholderGenerator = new PlaceholderGenerator();


function replaceExternalPlaceholders() {
    const images = document.querySelectorAll('img[src*="via.placeholder.com"]');
    
    images.forEach(img => {
        const src = img.src;
        const url = new URL(src);
        const pathname = url.pathname;
        const searchParams = url.searchParams;
        
        
        const sizeMatch = pathname.match(/(\d+)x(\d+)/);
        if (sizeMatch) {
            const width = parseInt(sizeMatch[1]);
            const height = parseInt(sizeMatch[2]);
            const text = searchParams.get('text') || '';
            
            
            let newSrc;
            if (src.includes('banner') || text.includes('Banner') || text.includes('Loading')) {
                newSrc = window.placeholderGenerator.generateBannerPlaceholder(width, height, text);
            } else if (src.includes('product') || text.includes('商品') || text.includes('Product')) {
                newSrc = window.placeholderGenerator.generateProductPlaceholder(width, height, text);
            } else {
                newSrc = window.placeholderGenerator.generateGenericPlaceholder(width, height, text);
            }
            
            img.src = newSrc;
            console.log('替换占位图片:', src, '->', newSrc.substring(0, 50) + '...');
        }
    });
}


function getPlaceholderImage(width, height, text = '', type = 'generic') {
    switch (type) {
        case 'product':
            return window.placeholderGenerator.generateProductPlaceholder(width, height, text);
        case 'banner':
            return window.placeholderGenerator.generateBannerPlaceholder(width, height, text);
        default:
            return window.placeholderGenerator.generateGenericPlaceholder(width, height, text);
    }
}


document.addEventListener('DOMContentLoaded', function() {
    setTimeout(() => {
        replaceExternalPlaceholders();
    }, 100);
});


if (typeof module !== 'undefined' && module.exports) {
    module.exports = {
        PlaceholderGenerator,
        replaceExternalPlaceholders,
        getPlaceholderImage
    };
}