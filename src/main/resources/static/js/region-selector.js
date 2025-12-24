class RegionSelector {
    constructor() {
        this.regionsData = null;
        this.loading = false;
    }


    async loadRegionsData() {
        if (this.regionsData || this.loading) {
            return this.regionsData;
        }

        this.loading = true;
        try {
            const response = await fetch('/api/home/regions');
            if (!response.ok) {
                throw new Error(`加载地区数据失败: ${response.status}`);
            }
            this.regionsData = await response.json();
            return this.regionsData;
        } catch (error) {
            console.error('加载地区数据出错:', error);
            throw error;
        } finally {
            this.loading = false;
        }
    }


    async init(provinceSelector, citySelector, areaSelector, options = {}) {
        try {
            await this.loadRegionsData();
            
            const provinceElement = document.querySelector(provinceSelector);
            const cityElement = document.querySelector(citySelector);
            const areaElement = document.querySelector(areaSelector);

            if (!provinceElement || !cityElement || !areaElement) {
                console.error('未找到地区选择器元素', {
                    provinceSelector,
                    citySelector,
                    areaSelector
                });
                return;
            }

            this.clearSelector(provinceElement);
            this.clearSelector(cityElement);
            this.clearSelector(areaElement);

            provinceElement.add(new Option('请选择省份', ''));
            cityElement.add(new Option('请选择城市', ''));
            areaElement.add(new Option('请选择区县', ''));

            cityElement.disabled = true;
            areaElement.disabled = true;

            this.regionsData.forEach(province => {
                const option = new Option(province.province, province.province);
                provinceElement.add(option);
            });

            provinceElement.addEventListener('change', () => {
                const selectedProvince = provinceElement.value;

                this.clearSelector(cityElement, true);
                this.clearSelector(areaElement, true);
                
                if (selectedProvince) {
                    cityElement.disabled = false;

                    const provinceData = this.regionsData.find(p => p.province === selectedProvince);
                    if (provinceData && provinceData.citys) {
                        provinceData.citys.forEach(city => {
                            cityElement.add(new Option(city.city, city.city));
                        });
                    }

                    if (options.defaultCity && (!options.defaultProvince || options.defaultProvince === selectedProvince)) {
                        const hasCityOption = Array.from(cityElement.options).some(opt => opt.value === options.defaultCity);
                        if (hasCityOption) {
                            cityElement.value = options.defaultCity;
                            const event = new Event('change');
                            cityElement.dispatchEvent(event);
                        }
                    }

                    if (options.onProvinceChange) {
                        options.onProvinceChange(selectedProvince);
                    }
                } else {
                    cityElement.disabled = true;
                    areaElement.disabled = true;
                }
            });

            cityElement.addEventListener('change', () => {
                const selectedProvince = provinceElement.value;
                const selectedCity = cityElement.value;

                this.clearSelector(areaElement, true);
                
                if (selectedProvince && selectedCity) {
                    areaElement.disabled = false;

                    const provinceData = this.regionsData.find(p => p.province === selectedProvince);
                    if (provinceData && provinceData.citys) {
                        const cityData = provinceData.citys.find(c => c.city === selectedCity);
                        if (cityData && cityData.areas) {
                            cityData.areas.forEach(area => {
                                areaElement.add(new Option(area.area, area.area));
                            });
                        }
                    }

                    if (options.defaultArea && (!options.defaultCity || options.defaultCity === selectedCity)) {
                        const hasAreaOption = Array.from(areaElement.options).some(opt => opt.value === options.defaultArea);
                        if (hasAreaOption) {
                            areaElement.value = options.defaultArea;

                            if (options.onAreaChange) {
                                options.onAreaChange(areaElement.value);
                            }
                        }
                    }

                    if (options.onCityChange) {
                        options.onCityChange(selectedCity);
                    }
                } else {
                    areaElement.disabled = true;
                }
            });

            if (options.onAreaChange) {
                areaElement.addEventListener('change', () => {
                    options.onAreaChange(areaElement.value);
                });
            }

            if (options.defaultProvince) {
                provinceElement.value = options.defaultProvince;
                setTimeout(() => {
                    const event = new Event('change');
                    provinceElement.dispatchEvent(event);
                }, 10);
            } else if (options.defaultCity) {
                for (const province of this.regionsData) {
                    if (province.citys && province.citys.some(city => city.city === options.defaultCity)) {
                        provinceElement.value = province.province;
                        setTimeout(() => {
                            const event = new Event('change');
                            provinceElement.dispatchEvent(event);
                        }, 10);
                        break;
                    }
                }
            }
        } catch (error) {
            console.error('初始化地区选择器出错:', error);
        }
    }

    clearSelector(selector, keepDefault = false) {
        const defaultOption = keepDefault ? selector.querySelector('option[value=""]') : null;

        while (selector.firstChild) {
            selector.removeChild(selector.firstChild);
        }

        if (defaultOption) {
            selector.appendChild(defaultOption);
        }
    }


    getSelectedRegions(provinceSelector, citySelector, areaSelector) {
        const provinceElement = document.querySelector(provinceSelector);
        const cityElement = document.querySelector(citySelector);
        const areaElement = document.querySelector(areaSelector);

        return {
            province: provinceElement ? provinceElement.value : '',
            city: cityElement ? cityElement.value : '',
            area: areaElement ? areaElement.value : ''
        };
    }


setSelectedRegions(provinceSelector, citySelector, areaSelector, regions) {
    const provinceElement = document.querySelector(provinceSelector);
    const cityElement = document.querySelector(citySelector);
    const areaElement = document.querySelector(areaSelector);

    if (!provinceElement || !cityElement || !areaElement) {
        console.error('未找到地区选择器元素', {
            provinceSelector, 
            citySelector, 
            areaSelector,
            foundProvince: !!provinceElement,
            foundCity: !!cityElement,
            foundArea: !!areaElement
        });
        return;
    }

    console.log('设置地区选择器值:', regions);

    const setRegions = () => {
        try {
            this._setRegionsInternal(provinceElement, cityElement, areaElement, regions);
        } catch (error) {
            console.error('设置地区选择器值失败:', error);
            setTimeout(() => {
                console.log('重试设置地区选择器值');
                try {
                    this._setRegionsInternal(provinceElement, cityElement, areaElement, regions);
                } catch (retryError) {
                    console.error('重试设置地区选择器值失败:', retryError);
                }
            }, 200);
        }
    };

    if (!this.regionsData) {
        this.loadRegionsData().then(() => {
            setRegions();
        }).catch(err => {
            console.error('加载地区数据失败:', err);
        });
    } else {
        setRegions();
    }
}


_setRegionsInternal(provinceElement, cityElement, areaElement, regions) {
    if (regions.province) {
        const hasProvinceOption = Array.from(provinceElement.options).some(opt => opt.value === regions.province);
        
        if (hasProvinceOption) {
            provinceElement.value = regions.province;

            this._loadAndSetCities(provinceElement.value, cityElement, regions.city, areaElement, regions.area);
        } else {
            console.warn(`省份选项"${regions.province}"不存在于选择器中`);

            if (this.regionsData) {
                const provinceData = this.regionsData.find(p => p.province === regions.province);
                if (provinceData) {
                    const option = new Option(provinceData.province, provinceData.province);
                    provinceElement.add(option);
                    provinceElement.value = provinceData.province;

                    this._loadAndSetCities(provinceData.province, cityElement, regions.city, areaElement, regions.area);
                }
            }
        }
    }
}


_loadAndSetCities(provinceName, cityElement, targetCity, areaElement, targetArea) {
    cityElement.disabled = false;

    this.clearSelector(cityElement, true);

    const provinceData = this.regionsData.find(p => p.province === provinceName);
    
    if (provinceData && provinceData.citys) {
        provinceData.citys.forEach(city => {
            cityElement.add(new Option(city.city, city.city));
        });

        if (targetCity) {
            const hasCityOption = Array.from(cityElement.options).some(opt => opt.value === targetCity);
            
            if (hasCityOption) {
                cityElement.value = targetCity;

                this._loadAndSetAreas(provinceName, targetCity, areaElement, targetArea);
            } else {
                console.warn(`城市选项"${targetCity}"不存在于选择器中`);

                const cityData = provinceData.citys.find(c => c.city === targetCity);
                if (cityData) {
                    const option = new Option(cityData.city, cityData.city);
                    cityElement.add(option);
                    cityElement.value = cityData.city;

                    this._loadAndSetAreas(provinceName, targetCity, areaElement, targetArea);
                }
            }
        }
    }
}


_loadAndSetAreas(provinceName, cityName, areaElement, targetArea) {
    areaElement.disabled = false;

    this.clearSelector(areaElement, true);

    const provinceData = this.regionsData.find(p => p.province === provinceName);
    
    if (provinceData && provinceData.citys) {
        const cityData = provinceData.citys.find(c => c.city === cityName);
        
        if (cityData && cityData.areas) {
            cityData.areas.forEach(area => {
                areaElement.add(new Option(area.area, area.area));
            });

            if (targetArea) {
                const hasAreaOption = Array.from(areaElement.options).some(opt => opt.value === targetArea);
                
                if (hasAreaOption) {
                    areaElement.value = targetArea;
                } else {
                    console.warn(`区县选项"${targetArea}"不存在于选择器中`);

                    const areaData = cityData.areas.find(a => a.area === targetArea);
                    if (areaData) {
                        const option = new Option(areaData.area, areaData.area);
                        areaElement.add(option);
                        areaElement.value = areaData.area;
                    }
                }
            }
        }
    }
}
}

defaultRegionSelector = new RegionSelector();