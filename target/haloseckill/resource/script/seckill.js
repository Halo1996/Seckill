//存放主要交互逻辑js代码
//javascript 模块化
//seckill.detail.init(params)
var seckill={
    //封装秒杀相关ajax的url
    URL:{

    },
    validatephone:function(phone) {
        if (phone && phone.length == 11 && !isNAN(phone)) {
            return true;
        } else {
            return false;
        }
    },
    //详情页秒杀逻辑
    detail:{
        //详情页初始化
        init:function (params) {
            //手机验证和登陆，计时交互
            //规划我们的交互流程
            //在cookie中查找手机号
            var killPhone=$.cookie('killPhone');
            var startTime=params['startTime'];
            var endTime=params['endTime'];
            var seckillId = params['seckillId'];
            if (!seckill.validatephone(killPhone)){
                //绑定phone
                var killPhoneModal=$("#killPhoneModal");
                killPhoneModal.modal({
                   show:true,//显示弹出层
                    backdrop:'static',//禁止位置关闭
                    keyboard:false//关闭键盘事件
                });
                $("#killPhoneBtn").click(function () {
                    var inputPhone=$('killPhoneKey').val();
                    if (seckill.validatephone(inputPhone)){
                        //电话写入cookie
                        $.cookie('killPhone',inputPhone,{expires:7,path:'/seckill'})
                        //刷新页面
                        window.location.reload();
                    }else{
                        $('#killPhoneMessage').hide().html('<label class="label label-danger">手机号错误！</label>').show(300);

                    }
                })
            }
        }
    }
}