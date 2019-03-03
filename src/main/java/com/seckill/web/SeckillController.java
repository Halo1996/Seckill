package com.seckill.web;

import com.seckill.dto.Exposer;
import com.seckill.dto.SeckillExcution;
import com.seckill.dto.SeckillResult;
import com.seckill.entity.Seckill;
import com.seckill.enums.SeckillStateEnum;
import com.seckill.exception.RepeatKillException;
import com.seckill.exception.SeckillCloseException;
import com.seckill.exception.SeckillException;
import com.seckill.service.SeckillService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@Controller//@Service @Component
@RequestMapping("/seckill")//url:/模块/资源/{id}/细分  /seckill/list
public class SeckillController {

    @Autowired
    private SeckillService seckillService;

    @RequestMapping(value = "/list",method = RequestMethod.GET)
    public String list(Model model){
        //获取列表页

        List<Seckill> list=seckillService.getSeckillList();
        model.addAttribute("list",list);
        //list.jsp+Model  =  ModelAndView
        return "list";///WEB-INF/jsp/list.jsp
    }

    @RequestMapping(value="/{seckillId}/detail",method = RequestMethod.GET)
    public String detail(@PathVariable("seckillId") Long seckillId, Model model){
        //获取详情页
        if(seckillId==null){
            return "redirect:/seckill/list";
        }
        Seckill seckill=seckillService.getById(seckillId);
        if(seckill==null){
            return "forward:/seckill/list";
        }
        model.addAttribute("seckill",seckill);
        return "detail";
    }

    //ajax json
    @RequestMapping(value = "/{seckillId}/exposer",
            method = RequestMethod.POST,
            produces = {"application/json;charset=UTF-8"}
    )
    @ResponseBody
    public SeckillResult<Exposer> exposer(@PathVariable Long seckillId){

        SeckillResult<Exposer> result;
        try{
            Exposer exposer=seckillService.exportSeckillUrl(seckillId);
            result =new SeckillResult<Exposer>(true,exposer);
        }catch(Exception e){

            result =new SeckillResult<Exposer>(false,e.getMessage());
        }
        return result;
    }

    @RequestMapping(value = "/{seckillId}/{md5}/excution",
            method = RequestMethod.POST,
            produces = {"application/json;charset=UTF-8"}
    )
    @ResponseBody
    public SeckillResult<SeckillExcution> excute(@PathVariable("seckillId") Long seckillId,
                                                  @PathVariable("md5") String md5,
                                                  @CookieValue(value = "killPhone",required = false)
                                                  Long phone){
        if (phone==null){
            return new SeckillResult<SeckillExcution>(false,"未注册");
        }
        SeckillResult<SeckillExcution> result;
        try {
            SeckillExcution excution= seckillService.excuteSeckill(seckillId,phone,md5);
            return new SeckillResult<SeckillExcution>(true,excution);
        }catch(RepeatKillException e){
            SeckillExcution excution=new SeckillExcution(seckillId, SeckillStateEnum.REPEAT_KILL);
            return new SeckillResult<SeckillExcution>(true,excution);
        }catch(SeckillCloseException e){
            SeckillExcution excution=new SeckillExcution(seckillId, SeckillStateEnum.END);
            return new SeckillResult<SeckillExcution>(true,excution);
        }catch (Exception e){
            SeckillExcution excution=new SeckillExcution(seckillId, SeckillStateEnum.INNER_ERROR);
            return new SeckillResult<SeckillExcution>(true,excution);
        }
    }

    @RequestMapping(value = "/time/now",method = RequestMethod.GET)
    @ResponseBody
    public SeckillResult<Long> time(){
        Date now = new Date();
        return new SeckillResult(true,now.getTime());
    }
}
