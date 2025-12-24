package com.zpyx.zhongpingyouxuan.dto.response;

public class MerchantLoginResponse {
    private String token;
    private MerchantDto merchant;

    
    public MerchantLoginResponse() {}

    public MerchantLoginResponse(String token, MerchantDto merchant) {
        this.token = token;
        this.merchant = merchant;
    }

    
    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public MerchantDto getMerchant() {
        return merchant;
    }

    public void setMerchant(MerchantDto merchant) {
        this.merchant = merchant;
    }

    
    public static class MerchantDto {
        private Long id;
        private String name;
        private String description;
        private String address;
        private String ownerUsername;

        
        public MerchantDto() {}

        public MerchantDto(Long id, String name, String description, String address, String ownerUsername) {
            this.id = id;
            this.name = name;
            this.description = description;
            this.address = address;
            this.ownerUsername = ownerUsername;
        }

        
        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public String getOwnerUsername() {
            return ownerUsername;
        }

        public void setOwnerUsername(String ownerUsername) {
            this.ownerUsername = ownerUsername;
        }
    }
}