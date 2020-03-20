function runIndex() {
	
'use strict';



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


}
