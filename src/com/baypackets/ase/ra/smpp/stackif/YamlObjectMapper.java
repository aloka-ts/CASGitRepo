package com.baypackets.ase.ra.smpp.stackif;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.log4j.Logger;

public class YamlObjectMapper {

	private static Logger logger = Logger.getLogger(YamlObjectMapper.class);
    public static RuleConfig getSmppServerConfig(File file)
    {
        RuleConfig ruleConfig = null;
        ObjectMapper om = new ObjectMapper(new YAMLFactory());


        try {
            ruleConfig = om.readValue(file, RuleConfig.class);
            logger.debug("Rule object initlized :"+ ruleConfig);
        }

        catch (IOException e) {
        	logger.error("Error in reading or deserialaizing rules file from Path :-"+ file.getAbsolutePath() +"due to" +e);
        }

        return ruleConfig;

    }

}
