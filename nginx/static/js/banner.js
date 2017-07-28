$(document).ready(function() {
	var interval=0;
	var now=0;
	var ready=true;
	var banners=$(".wrap-content");
	var bannerBox=$(".wrap-slides");
	var leftC =$(".bannerLeft_click");
	var rightC =$(".bannerRight_click");
	function playBanner() {
		clearInterval(interval);
		interval=setInterval(function() {
			if(banners.length <= 1) clearInterval(interval);
			var next= now+1 >= banners.length ? 0 : now+1;
			var currObj=banners.eq(now);
			var nextObj=banners.eq(next);
			currObj.stop(true).fadeOut(1000);
			nextObj.stop(true).fadeIn(1000);
			
			$(".dotnav li").removeClass("wrap-trigger");
			$(".dotnav li").eq(next).addClass("wrap-trigger");
			now=next;
		},5000)
	};

	playBanner();

	$(".dotnav li").bind("click",function() {
		var index=$(this).prevAll("li").length;
		if(index == now) return;
		clearInterval(interval);
		
		var currObj=banners.eq(now);
		var nextObj=banners.eq(index);
		currObj.stop(true).fadeOut(1000);
		nextObj.stop(true).fadeIn(1000);
		
		$(".dotnav li").removeClass("wrap-trigger");
		$(this).addClass("wrap-trigger");
		now=index;
		playBanner();
	})

	bannerBox.mouseover(function(){
		leftC.fadeIn();
		rightC.fadeIn();
		clearInterval(interval);
	})

	bannerBox.mouseleave(function(){
		leftC.fadeOut();
		rightC.fadeOut();
		playBanner();
	})

	leftC.bind("click",function(){
		var index=$(".wrap-trigger").index();
		var prev= index-1 < -1 ? banners.length : index-1;
		var currObj=banners.eq(now);
		var nextObj=banners.eq(prev);
		if(!ready)return;
		ready=false;
		currObj.stop(true).fadeOut(1000);
		nextObj.stop(true).fadeIn(1000,function(){ready=true;});
		$(".dotnav li").removeClass("wrap-trigger");
		$(".dotnav li").eq(prev).addClass("wrap-trigger");
		now=prev;
		playBanner();
	})

	rightC.bind("click",function(){
		// var index=$(".wrap-trigger").index();
		var next= now+1 >= banners.length ? 0 : now+1;
		var currObj=banners.eq(now);
		var nextObj=banners.eq(next);
		if(!ready)return;
		ready=false;
		currObj.stop(true).fadeOut(1000);
		nextObj.stop(true).fadeIn(1000,function(){ready=true;});
		$(".dotnav li").removeClass("wrap-trigger");
		$(".dotnav li").eq(next).addClass("wrap-trigger");
		now=next;
		playBanner();
	})
})