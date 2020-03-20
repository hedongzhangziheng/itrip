function runStrategy() {
    /**
 * Created by zhzbin on 2016/11/24.
 */
var strategy = {
    init: function (e) {
        var sef = this;

        sef.initchart();
        sef.banner(".banner-border");
        sef.initTabs();
        sef.bindEvent();
    },
    banner: function (e) {
// 首页轮播图
        $(".banner-li1").attr("background","../");
        var num = 0;
        var timer = null;
        var i_circle = $(e + ' .lb li');
        var i_banner = $('.banner_list li');
        var i=5;
        $("a",i_banner).each(function (e) {
            this.style.background="url(../images/img/strategy/strategy-0"+i+".jpg)";
            i++;
        });
// 自动轮播
        function autoPlay() {
            timer = setInterval(function () {
                num++;
                if (num == i_circle.length + 1) {
                    num = 0;
                }
                i_circle.eq(num).addClass('cur').siblings().removeClass('cur');
                i_banner.eq(num).fadeIn(60).siblings().fadeOut();
            }, 3000);
        }

//调用自动轮播
        autoPlay();
// 经过小圆点切换
        i_circle.hover(function () {
            clearInterval(timer);
            $(this).addClass('cur').siblings().removeClass('cur');
            num = $(this).index();
            i_banner.eq(num).fadeIn(60).siblings().fadeOut();
        }, function () {
            autoPlay();
        });
// 鼠标经过banner图事件
        i_banner.hover(function () {
            clearInterval(timer);
        }, function () {
            autoPlay();
        });
    },
    initTabs:function(e){
        $(".strategy-tabs .tabs-header li").bind("click",function (e) {
            if(!$(this).hasClass("active")){
                $("li",$(this).closest("ul")).removeClass("active");
                $(this).addClass("active")
            }
        });
    },
    initchart: function (e) {
        var myChart = echarts.init(document.getElementById("linkForChina"));
        var option = {
            baseOption: {
                timeline: {
                    // y: 0,
                    axisType: 'category',
                    realtime: false,
                    loop: false,
                    autoPlay: false,
                    // currentIndex: 2,
                    playInterval: 10,
                    controlStyle: {
                        position: 'left'
                    },
                    data: [
                        '2016/11/19', '2016/11/20', '2016/11/21'
                    ],
//             label: {
//                 formatter : function(s) {
//                     return (new Date(s)).getFullYear();
//                 }
//             },
                    timelinePlayChange: function () {
                        console.debug("dasdasd");
                    }
                },
                title: {
                    text: '预计三天内全国旅游景点旅游人数图',
                    subtext: 'http://qmjie.sinaapp.com/apple/',
                    left: 'center'
                },
                tooltip: {
                    trigger: 'item',
                    formatter: function (params) {
                        var MAP_VALUE_DIC = {
                            '5': '畅通',
                            '10': '缓慢',
                            '15': '拥堵'
                        }
                        if (params.seriesType) {
                            return params.name + ': ' + MAP_VALUE_DIC[params.value];
                        } else {
                            return params.name;
                        }
                        console.log(params);
                    }
                },

                visualMap: {
                    type: 'piecewise',
                    splitNumber: 3,
                    piecewise: [
                        {
                            value: 1,
                            label: '旅游人数<5w',
                            color: 'orangered'
                        }, {
                            value: 5,
                            label: '5w<旅游人数<15w',
                            color: 'yellow'
                        }, {
                            value: 15,
                            label: '15w<旅游人数',
                            color: 'lightskyblue'
                        }

                    ],
                    min: 0,
                    max: 20,
                    left: 'right',
                    top: 'middle',
                    calculable: true,
                    show: true,
                    seriesIndex: 0,
                    inRange: {
                        color: ['yellow', 'lightskyblue']
                    },
                    formatter: function (value) {
                        if (value <5) {
                            return '旅游人数<5w';
                        } else if (value > 5 && value < 15) {
                            return '5w<旅游人数<15w';
                        } else {
                            return '15w>旅游人数';
                        }
                    }
                }
            },
            options: [
                {
                    series: [{
                        name: '全国旅游人数统计',
                        type: 'map',
                        mapType: 'china',
                        animationDelay: function (idx) {
                            return idx * 10 + 10;
                        },
                        label: {
                            normal: {
                                show: false
                            },
                            emphasis: {
                                show: true
                            }
                        },
                        data: [
                            {
                                name: '重庆',
                                value: 50
                            }, {
                                name: '云南',
                                value: 50
                            }, {
                                name: '辽宁',
                                value: 50
                            }, {
                                name: '黑龙江',
                                value: 50
                            }, {
                                name: '广西',
                                value: 50
                            }, {
                                name: '甘肃',
                                value: 50
                            }, {
                                name: '山西',
                                value: 50
                            }, {
                                name: '陕西',
                                value: 50
                            }, {
                                name: '吉林',
                                value: 50
                            }, {
                                name: '贵州',
                                value: 50
                            }, {
                                name: '新疆',
                                value: 50
                            }, {
                                name: '青海',
                                value: 50
                            }, {
                                name: '西藏',
                                value: 50
                            }, {
                                name: '四川',
                                value: 50
                            }, {
                                name: '宁夏',
                                value: 50
                            }, {
                                name: '海南',
                                value: 50
                            }, {
                                name: '台湾',
                                value: 50
                            }, {
                                name: '香港',
                                value: 50
                            }, {
                                name: '澳门',
                                value: 50
                            }, {
                                name: '上海',
                                value: 20
                            }, {
                                name: '安徽',
                                value: 20
                            }, {
                                name: '江苏',
                                value: 20
                            }, {
                                name: '浙江',
                                value: 20
                            }, {
                                name: '北京',
                                value: 10
                            }, {
                                name: '天津',
                                value: 10
                            }, {
                                name: '河北',
                                value: 10
                            }, {
                                name: '河南',
                                value: 10
                            }, {
                                name: '内蒙古',
                                value: 10
                            }, {
                                name: '湖南',
                                value: 10
                            }, {
                                name: '山东',
                                value: 10
                            }, {
                                name: '江西',
                                value: 10
                            }, {
                                name: '湖北',
                                value: 10
                            }, {
                                name: '福建',
                                value: 10
                            }, {
                                name: '广东',
                                value: 10
                            }]
                    }]
                },
                {
                    series: [{
                        name: '全国旅游人数统计',
                        type: 'map',
                        mapType: 'china',
                        label: {
                            normal: {
                                show: false
                            },
                            emphasis: {
                                show: true
                            }
                        },
                        data: [
                            {
                                name: '重庆',
                                value: 25
                            }, {
                                name: function (e) {
                                    var name = "云南";
                                    name.prototype = {apram: "", params: "param"};
                                    return name;
                                }, value: 25
                            }, {
                                name: '辽宁',
                                value: 25
                            }, {
                                name: '黑龙江',
                                value: 25
                            }, {
                                name: '广西',
                                value: 25
                            }, {
                                name: '甘肃',
                                value: 25
                            }, {
                                name: '山西',
                                value: 25
                            }, {
                                name: '陕西',
                                value: 25
                            }, {
                                name: '吉林',
                                value: 25
                            }, {
                                name: '贵州',
                                value: 25
                            }, {
                                name: '新疆',
                                value: 25
                            }, {
                                name: '青海',
                                value: 25
                            }, {
                                name: '西藏',
                                value: 25
                            }, {
                                name: '四川',
                                value: 25
                            }, {
                                name: '宁夏',
                                value: 25
                            }, {
                                name: '海南',
                                value: 25
                            }, {
                                name: '台湾',
                                value: 25
                            }, {
                                name: '香港',
                                value: 25
                            }, {
                                name: '澳门',
                                value: 25
                            }, {
                                name: '上海',
                                value: 4
                            }, {
                                name: '安徽',
                                value: 4
                            }, {
                                name: '江苏',
                                value: 4
                            }, {
                                name: '浙江',
                                value: 4
                            }, {
                                name: '北京',
                                value: 10
                            }, {
                                name: '天津',
                                value: 10
                            }, {
                                name: '河北',
                                value: 10
                            }, {
                                name: '河南',
                                value: 10
                            }, {
                                name: '内蒙古',
                                value: 10
                            }, {
                                name: '湖南',
                                value: 10
                            }, {
                                name: '山东',
                                value: 10
                            }, {
                                name: '江西',
                                value: 10
                            }, {
                                name: '湖北',
                                value: 10
                            }, {
                                name: '福建',
                                value: 10
                            }, {
                                name: '广东',
                                value: 10
                            }]
                    }]
                },
                {
                    series: [{
                        name: '全国旅游人数统计',
                        type: 'map',
                        mapType: 'china',
                        label: {
                            normal: {
                                show: false
                            },
                            emphasis: {
                                show: true
                            }
                        },
                        data: [
                            {
                                name: '重庆',
                                value: 10
                            }, {
                                name: '云南',
                                value: 10
                            }, {
                                name: '辽宁',
                                value: 10
                            }, {
                                name: '黑龙江',
                                value: 10
                            }, {
                                name: '广西',
                                value: 10
                            }, {
                                name: '甘肃',
                                value: 10
                            }, {
                                name: '山西',
                                value: 10
                            }, {
                                name: '陕西',
                                value: 10
                            }, {
                                name: '吉林',
                                value: 10
                            }, {
                                name: '贵州',
                                value: 10
                            }, {
                                name: '新疆',
                                value: 10
                            }, {
                                name: '青海',
                                value: 10
                            }, {
                                name: '西藏',
                                value: 10
                            }, {
                                name: '四川',
                                value: 10
                            }, {
                                name: '宁夏',
                                value: 10
                            }, {
                                name: '海南',
                                value: 10
                            }, {
                                name: '台湾',
                                value: 10
                            }, {
                                name: '香港',
                                value: 10
                            }, {
                                name: '澳门',
                                value: 10
                            }, {
                                name: '上海',
                                value: 4
                            }, {
                                name: '安徽',
                                value: 4
                            }, {
                                name: '江苏',
                                value: 4
                            }, {
                                name: '浙江',
                                value: 4
                            }, {
                                name: '北京',
                                value: 25
                            }, {
                                name: '天津',
                                value: 25
                            }, {
                                name: '河北',
                                value: 25
                            }, {
                                name: '河南',
                                value: 25
                            }, {
                                name: '内蒙古',
                                value: 25
                            }, {
                                name: '湖南',
                                value: 25
                            }, {
                                name: '山东',
                                value: 25
                            }, {
                                name: '江西',
                                value: 25
                            }, {
                                name: '湖北',
                                value: 25
                            }, {
                                name: '福建',
                                value: 25
                            }, {
                                name: '广东',
                                value: 25
                            }]
                    }]
                }
            ]
        };
        myChart.setOption(option);
        myChart.on("click", function (e) {
            $(".localtion-mes strong").html(e.name);
        });


    },
    bindEvent: function (e) {
        $(function () {
            var test = new Vcity.CitySelector({
                input: 'travelCitySelect1'
            });
        });


    }, refresh: function (e) {
        alert("刷新函数是否正常使用？")
    }
};


//
//调转页面
$(".strategy").click(function(){
    self.location='../methodDetail.html'; 
})
strategy.init();
}
