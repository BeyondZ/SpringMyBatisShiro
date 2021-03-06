package com.lance.shiro.service;

import com.gitee.sunchenbin.mybatis.actable.manager.common.BaseMysqlCRUDManager;
import com.lance.shiro.entity.IProperty;
import com.lance.shiro.mapper.PropertyMapper;
import com.lance.shiro.mapper.UserMapper;
import com.lance.shiro.utils.ConvertUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Map;

import static com.lance.shiro.config.ConstantVariable.*;

@Service
@Transactional
public class PropertyServiceImpl implements PropertyService {
    @Autowired
    private PropertyMapper propertyMapper;

    @Autowired
    private CommonService commonService;

    @Autowired
    private UMailService uMailService;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private BaseMysqlCRUDManager baseMysqlCRUDManager;


    @Override
    public Map get(int id) {
        IProperty property = propertyMapper.get(id);
        return setAttachment(property);
    }

    @Override
    public Map save(IProperty property) throws IllegalAccessException {
        if (property.getId() == 0) {
            propertyMapper.add(property);
        } else {
//            Field fields[] = property.getClass().getDeclaredFields();
//            StringBuffer sb = new StringBuffer();
//            for (int i = 0; i < fields.length; i++) {
//                fields[i].setAccessible(true);
//                String keyName = fields[i].getName();
//                // 处理空值和实体 agent，owner，propertyList
//                if (!keyName.equals("agent") && !keyName.equals("owner") && !keyName.equals("propertyList") && !keyName.equals("qty")) {
//                    String value = fields[i].get(property) == null ? "" : fields[i].get(property).toString();
//                    sb.append(keyName).append("=").append("'").append(fields[i].get(property)).append("'").append(" ").append(",");
//                }
//            }
//            if (null != sb) {
//                String s = sb.toString();
//                propertyMapper.update(property.getId(), s.substring(0, s.length() - 1));
//                property = propertyMapper.get(property.getId());
//            }
            propertyMapper.update(property);
        }
        return setAttachment(property);
    }

    @Override
    public void deleteAllByIds(ArrayList<Integer> ids) {
        int size = ids.size();
        for (int i = 0; i < size; i++) {
            delete(ids.get(i));
        }
    }

    @Override
    public ArrayList<Map> findAllByAgents(ArrayList<Integer> agentId) {
        ArrayList<IProperty> list;
        if (agentId != null && agentId.size() > 0) {
            String agentIds = "'" + StringUtils.join(agentId, "','") + "'";
            list = propertyMapper.findAllByAgent(agentIds);
        } else {
            list = propertyMapper.findAll();
        }
        return setAttachmentForList(list);
    }

    @Override
    public ArrayList<Map> findAllByOwners(ArrayList<Integer> ownerId) {
        ArrayList<IProperty> list;
        if (ownerId != null && ownerId.size() > 0) {
            String ownerIds = "'" + StringUtils.join(ownerId, "','") + "'";
            list = propertyMapper.findAllByOwner(ownerIds);
        } else {
            list = propertyMapper.findAll();
        }
//        List<Test> query = baseMysqlCRUDManager.query(test);
        return setAttachmentForList(list);
    }

    /**
     * @param reqMap
     * @return
     */
    @Override
    public ArrayList<Map> findAllByPropertyLists(Map<String, String> reqMap) {
        ArrayList<IProperty> list;
        if (null != reqMap && reqMap.size() > 0) {
            IProperty iProperty = new IProperty();
            Field fields[] = iProperty.getClass().getDeclaredFields();
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < fields.length; i++) {
                String keyName = fields[i].getName();
                if (null != reqMap.get(keyName)) {
                    sb.append("  ").append(keyName).append("=").append("'").append(reqMap.get(keyName)).append("'").append("  ").append("and");
                }
            }
            if (null != sb) {
                String s = sb.toString();
                list = propertyMapper.findAllByPropertyList(s.substring(0, s.length() - 3));
            } else {
                list = propertyMapper.findAll();
            }
        } else {
            list = propertyMapper.findAll();
        }
        return setAttachmentForList(list);
    }

    /**
     * @param id
     * @param reqMap
     * @return
     */
    @Override
    public IProperty updateAttribute(int id, Map<String, String> reqMap) {
        IProperty iProperty = propertyMapper.get(id);
        if (null != iProperty) {
            Field fields[] = iProperty.getClass().getDeclaredFields();
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < fields.length; i++) {
                String keyName = fields[i].getName();
                if (null != reqMap.get(keyName)) {
                    sb.append("  ").append(keyName).append("=").append("'").append(reqMap.get(keyName)).append("'").append("  ").append(",");
                }
            }
            if (null != sb) {
                String s = sb.toString();
                propertyMapper.updateAttribute(iProperty.getId(), s.substring(0, s.length() - 1));
                iProperty = propertyMapper.get(id);
                return iProperty;
            }
        }
        return null;
    }

    @Override
    public IProperty notifysale(int id, Map<String, String> reqMap) throws Exception {
        IProperty iProperty = updateAttribute(id, reqMap);

        String to = userMapper.getAdminEmail();
        if(to != null && !to.equals("")){
            String subject = "property "+iProperty.getPropertyList().getBuildingName() + " "+ iProperty.getLot() +" has received a deposit";
            String body = "<div>Agent: "+iProperty.getAgent().getCode()+" "+
                    iProperty.getAgent().getFirstName() +" "+iProperty.getAgent().getLastName()+
                    " has submitted a property  deposit request for property: "+iProperty.getPropertyList().getBuildingName() + " "+ iProperty.getLot() +
                    " at time: "+ iProperty.getUpdateTime() +"</div>";
            body += "<div>Sales Notes: "+iProperty.getSale_note()+"</div>";
            body += "<div><a href=\"http://www.ipanproperty.com\" target='_blank'>http://www.ipanproperty.com</a></div>";
            uMailService.sendManagerMail(to, subject, body);
        }
        return iProperty;
    }


    @Override
    public void delete(Integer id) {
        propertyMapper.delete(id);
        String sid = String.valueOf(id);
        commonService.deleteListAttachmentByBelong(sid, BELONG_TO_CATEGORY_PROPERTY_COS_DOCUMENT);
        commonService.deleteListAttachmentByBelong(sid, BELONG_TO_CATEGORY_PROPERTY_DEPOSIT_FORM_DOCUMENT);
        commonService.deleteListAttachmentByBelong(sid, BELONG_TO_CATEGORY_PROPERTY_BILL_CHARGES);
        commonService.deleteListAttachmentByBelong(sid, BELONG_TO_CATEGORY_PROPERTY_MANAGEMENT_AGREEMENT_DOCUMENT);
        commonService.deleteListAttachmentByBelong(sid, BELONG_TO_CATEGORY_PROPERTY_OTHERS);
        commonService.deleteListAttachmentByBelong(sid, BELONG_TO_CATEGORY_PROPERTY_MAIN_IMAGE);
        commonService.deleteListAttachmentByBelong(sid, BELONG_TO_CATEGORY_PROPERTY_BANK_CARD_PICTURE);
    }

    private ArrayList<Map> setAttachmentForList(ArrayList<IProperty> list) {
        ArrayList<Map> aPropertys = new ArrayList<Map>();
        for (int i = 0, size = list.size(); i < size; i++) {
            Map maProperty = setAttachment(list.get(i));
            aPropertys.add(maProperty);
        }
        return aPropertys;
    }

    private Map setAttachment(IProperty property) {
        Map mproperty = ConvertUtils.beanToMap(property);
        String id = String.valueOf(property.getId());
        mproperty.put(BELONG_TO_CATEGORY_PROPERTY_COS_DOCUMENT, commonService.findListAttachmentByBelong(id, BELONG_TO_CATEGORY_PROPERTY_COS_DOCUMENT));
        mproperty.put(BELONG_TO_CATEGORY_PROPERTY_DEPOSIT_FORM_DOCUMENT, commonService.findListAttachmentByBelong(id, BELONG_TO_CATEGORY_PROPERTY_DEPOSIT_FORM_DOCUMENT));
        mproperty.put(BELONG_TO_CATEGORY_PROPERTY_BILL_CHARGES, commonService.findListAttachmentByBelong(id, BELONG_TO_CATEGORY_PROPERTY_BILL_CHARGES));
        mproperty.put(BELONG_TO_CATEGORY_PROPERTY_MANAGEMENT_AGREEMENT_DOCUMENT, commonService.findListAttachmentByBelong(id, BELONG_TO_CATEGORY_PROPERTY_MANAGEMENT_AGREEMENT_DOCUMENT));
        mproperty.put(BELONG_TO_CATEGORY_PROPERTY_OTHERS, commonService.findListAttachmentByBelong(id, BELONG_TO_CATEGORY_PROPERTY_OTHERS));
        mproperty.put(BELONG_TO_CATEGORY_PROPERTY_MAIN_IMAGE, commonService.findListAttachmentByBelong(id, BELONG_TO_CATEGORY_PROPERTY_MAIN_IMAGE));
        mproperty.put(BELONG_TO_CATEGORY_PROPERTY_BANK_CARD_PICTURE, commonService.findListAttachmentByBelong(id, BELONG_TO_CATEGORY_PROPERTY_BANK_CARD_PICTURE));
        return mproperty;
    }
}