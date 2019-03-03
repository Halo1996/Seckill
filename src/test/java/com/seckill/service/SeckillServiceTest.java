package com.seckill.service;

import com.seckill.dto.Exposer;
import com.seckill.dto.SeckillExcution;
import com.seckill.entity.Seckill;
import com.seckill.exception.RepeatKillException;
import com.seckill.exception.SeckillCloseException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({
        "classpath:spring/spring-dao.xml",
        "classpath:spring/spring-service.xml"})
public class SeckillServiceTest {

    private final Logger logger= LoggerFactory.getLogger(this.getClass());

    @Autowired
    private SeckillService seckillService;

    @Test
    public void testGetSeckillList() throws Exception {
        List<Seckill> list=seckillService.getSeckillList();
        logger.info("list={}",list);
    }


    @Test
    public void testGetById() throws Exception {
        long id=1000;
        Seckill seckill=seckillService.getById(id);
        logger.info("seckill={}",seckill);
    }

    //测试代码完整逻辑
    @Test
    public void testSeckillLogic() throws Exception{
        long id=1000;
        Exposer exposer=seckillService.exportSeckillUrl(id);
        if(exposer.isExposed()){
            logger.info("exposer={}",exposer);
            long phone=1238475789L;
            String md5=exposer.getMd5();
            try{
                SeckillExcution seckillExcution=seckillService.excuteSeckill(id,phone,md5);
                logger.info("result={}",seckillExcution);
            }catch (RepeatKillException e){
                logger.error(e.getMessage());
            }catch (SeckillCloseException e){
                logger.error(e.getMessage());
            }
        }else{
            //秒杀未开启
            logger.warn("exposer={}",exposer);
        }

    }

    @Test
    public void executeSeckillProcedure(){
        long seckillId = 1001;
        long phone=98465132345L;
        Exposer exposer=seckillService.exportSeckillUrl(seckillId);
        if (exposer.isExposed()){
            String md5 = exposer.getMd5();
            SeckillExcution excution=seckillService.executeSeckillProcedure(seckillId,phone,md5);
            logger.info(excution.getStateInfo());
        }
    }
}