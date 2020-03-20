function runFlyTicket() {


'use strict';
// 首页轮播图
var num = 0;
var timer = null;
var i_circle = $( '.banner .lb li' );
var i_banner = $( '.banner_list li' );
// 自动轮播
function autoPlay(){
	timer = setInterval( function(){
		num++;
		if(num == i_circle.length+1){
			num=0;
		}
		i_circle.eq(num).addClass( 'cur' ).siblings().removeClass('cur');
		i_banner.eq(num).fadeIn(600).siblings().fadeOut();
	},3000 );
}
//调用自动轮播
autoPlay();
// 经过小圆点切换
i_circle.hover( function(){
	clearInterval(timer);
	$( this ).addClass( 'cur' ).siblings().removeClass('cur');
	num = $( this ).index();
	i_banner.eq(num).fadeIn(600).siblings().fadeOut();
},function(){
	autoPlay();
	});
// 鼠标经过banner图事件
i_banner.hover(function(){
	clearInterval(timer);	
},function(){
	autoPlay();
	});

//tab栏切换
$( '.i_tab_l>li>a' ).click( function(){
	$( this ).parent('li').addClass('cur').siblings().removeClass('cur');
	$( this ).parent('li').addClass('show').siblings().removeClass('show');
} );

/*嵌套Tab*/
function doubleTab ( aEle , bEle , cls){
	$( aEle ).click( function(){
		$( this ).addClass(cls).siblings().removeClass(cls);
		var num = $( this ).index();
		$( bEle ).eq(num).addClass('i_show').siblings().removeClass('i_show');
	} );
}

doubleTab( '.travel .i_title>a' , '.travel .discount' , 'line');
doubleTab( '.hotel .i_title>a' , '.hotel .discount' , 'line');
doubleTab( '.fly .i_title>a' , '.fly .discount' , 'line');
doubleTab( '.idea .i_title>a' , '.idea .discount' , 'line');






/* CitySelector 初始化*/
var test_one=new Vcity.CitySelector({input:'citySelect_one'});
var test_two=new Vcity.CitySelector({input:'citySelect_two'});
var test_three=new Vcity.CitySelector({input:'citySelect_three'});
var test_four=new Vcity.CitySelector({input:'citySelect_four'});
var test_five=new Vcity.CitySelector({input:'citySelect_five'});
var test_six=new Vcity.CitySelector({input:'citySelect_six'});


var test_lineone=new Vcity.CitySelector({input:'citySelect_lineone'});
var test_linetwo=new Vcity.CitySelector({input:'citySelect_linetwo'});


/*折线图*/
var line = echarts.init(document.getElementById('flodline'));
line.setOption({
    title: {
        x: 'left',
        text: '机票价格走势图',
        textStyle: {
            fontSize: '18',
            color: '#4c4c4c',
            fontWeight: 'bolder'
        }
    },
    tooltip: {
        trigger: 'axis'
    },
    toolbox: {
        show: true,
        feature: {
            dataZoom: {
                yAxisIndex: 'none'
            },
            dataView: {readOnly: false},
            magicType: {type: ['line', 'bar']}
        }
    },
    xAxis:  {
        type: 'category',
        boundaryGap: false,
        data: ['12-01','12-10','12-20','12-30','01-01','01-10','01-20','01-30','02-01','02-10','02-20','02-30'],
        axisLabel: {
            interval:0
        }
    },
    yAxis: {
        type: 'value'
    },
    series: [
        {
            name:'北京-上海',
            type:'line',
            data:[723, 642, 618, 745, 648, 649,700, 745, 618, 765, 779,600],
            itemStyle : {  
                        normal : {  
                            lineStyle:{  
                                color:'#6be6c1'  
                            }  
                        }  
                    },
            markLine: {data: [{type: 'average', name: '平均值'}]}
        },
        {
            name:'北京-长沙',
            type:'line',
            data:[443, 352, 418, 345, 448, 349,300, 352, 418, 445, 348, 449,300],
            itemStyle : {  
                        normal : {  
                            lineStyle:{  
                                color:'#626c91'  
                            }  
                        }  
                    },
            markLine: {data: [{type: 'average', name: '平均值'}]}
        }
        ,
        {
            name:'北京-深圳',
            type:'line',
            data:[943, 752, 718, 885, 848, 890, 900, 972, 998, 825, 888, 949,800],
            itemStyle : {  
                        normal : {  
                            lineStyle:{  
                                color:'#3fb1e3'  
                            }  
                        }  
                    },
            markLine: {data: [{type: 'average', name: '平均值'}]}
        }
    ]
}) ;

// 图片上移代码

 $(".i-fly-salepro").find("li").hover(function() {
            $(this).stop().animate({"marginTop":"-6px"},300);
        }, function() {
            $(this).stop().animate({"marginTop":0},300);
        });


}
