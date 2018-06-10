package com.lance.shiro.mapper;
import com.lance.shiro.entity.*;
import org.apache.ibatis.annotations.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Mapper
public interface PropertyMapper {
    @Insert("insert into i_property (address,agentId,propertyListId,lot,buildingOverview,features,purchasePrice,sellingPrice,weeklyRent,managedDoma,lotTypeId,type,textContractOfSale,textDepositForm,textSolicitor,textBillsCharges,textManagementAgreement,textOthers,purchaseDate,commenceDate,commission_rent,commission_sale,term_of_lease,ownerId,createTime,updateTime,status) values(#{address},#{agentId},#{propertyListId},#{lot},#{buildingOverview},#{features},#{purchasePrice},#{sellingPrice},#{weeklyRent},#{managedDoma},#{lotTypeId},#{type},#{textContractOfSale},#{textDepositForm},#{textSolicitor},#{textBillsCharges},#{textManagementAgreement},#{textOthers},#{purchaseDate},#{commenceDate},#{commission_rent},#{commission_sale},#{term_of_lease},#{ownerId},now(),now(),'active')")
    @Options(useGeneratedKeys=true,keyProperty="id")
    public int add(IProperty property);

    @Update("update i_property set address = #{address},agentId = #{agentId},propertyListId = #{propertyListId},lot = #{lot},buildingOverview = #{buildingOverview},features = #{features},purchasePrice = #{purchasePrice},sellingPrice = #{sellingPrice},weeklyRent = #{weeklyRent},managedDoma = #{managedDoma},lotTypeId = #{lotTypeId},type = #{type},textContractOfSale = #{textContractOfSale},textDepositForm = #{textDepositForm},textSolicitor = #{textSolicitor},textBillsCharges = #{textBillsCharges},textManagementAgreement = #{textManagementAgreement},textOthers = #{textOthers},purchaseDate = #{purchaseDate},commenceDate = #{commenceDate},commission_rent = #{commission_rent},commission_sale = #{commission_sale},term_of_lease = #{term_of_lease},ownerId = #{ownerId},updateTime = now() where id=#{id} ")
    int  update(IProperty property);

    @Update("update i_property set status = 'inactive',updateTime = now()  where id=#{id} ")
    public void delete(int id);

    @Select("select * from i_property where id=#{id}")
    @Results({
        @Result(property="id",column="id"),
        @Result(property="agent",column="agentId",javaType=IUser.class,
            one=@One(select="com.lance.shiro.mapper.UserMapper.get")),
        @Result(property="propertyList",column="propertyListId",javaType=IPropertyList.class,
                one=@One(select="com.lance.shiro.mapper.PropertyListMapper.get")),
        @Result(property="lotType",column="lotTypeId",javaType=ILotType.class,
                    one=@One(select="com.lance.shiro.mapper.LotTypeMapper.get")),
        @Result(property="owner",column="ownerId",javaType=IUser.class,
                one=@One(select="com.lance.shiro.mapper.UserMapper.get")),
    })
    public IProperty get(int id);

    //根据PropertyList查询
    @Select("SELECT * FROM i_property where status = 'active' and  propertyListId in (${propertyListId})  ")
    @Results({
            @Result(property="id",column="id"),
            @Result(property="agent",column="agentId",javaType=IUser.class,
                    one=@One(select="com.lance.shiro.mapper.UserMapper.get")),
            @Result(property="propertyList",column="propertyListId",javaType=IPropertyList.class,
                    one=@One(select="com.lance.shiro.mapper.PropertyListMapper.get")),
            @Result(property="lotType",column="lotTypeId",javaType=ILotType.class,
                    one=@One(select="com.lance.shiro.mapper.LotTypeMapper.get")),
            @Result(property="owner",column="ownerId",javaType=IUser.class,
                    one=@One(select="com.lance.shiro.mapper.UserMapper.get")),
    })
    ArrayList<IProperty> findAllByPropertyList(@Param("propertyListId") String propertyListId);

    //根据Agent查询
    @Select("SELECT * FROM i_property where status = 'active' and  agentId in (${agentId})  ")
    @Results({
            @Result(property="id",column="id"),
            @Result(property="agent",column="agentId",javaType=IUser.class,
                    one=@One(select="com.lance.shiro.mapper.UserMapper.get")),
            @Result(property="propertyList",column="propertyListId",javaType=IPropertyList.class,
                    one=@One(select="com.lance.shiro.mapper.PropertyListMapper.get")),
            @Result(property="lotType",column="lotTypeId",javaType=ILotType.class,
                    one=@One(select="com.lance.shiro.mapper.LotTypeMapper.get")),
            @Result(property="owner",column="ownerId",javaType=IUser.class,
                    one=@One(select="com.lance.shiro.mapper.UserMapper.get")),
    })
    ArrayList<IProperty> findAllByAgent(@Param("agentId") String agentId);

    //根据Owner查询
    @Select("SELECT * FROM i_property where status = 'active' and  ownerId in (${ownerId})  ")
    @Results({
            @Result(property="id",column="id"),
            @Result(property="agent",column="agentId",javaType=IUser.class,
                    one=@One(select="com.lance.shiro.mapper.UserMapper.get")),
            @Result(property="propertyList",column="propertyListId",javaType=IPropertyList.class,
                    one=@One(select="com.lance.shiro.mapper.PropertyListMapper.get")),
            @Result(property="lotType",column="lotTypeId",javaType=ILotType.class,
                    one=@One(select="com.lance.shiro.mapper.LotTypeMapper.get")),
            @Result(property="owner",column="ownerId",javaType=IUser.class,
                    one=@One(select="com.lance.shiro.mapper.UserMapper.get")),
    })
    ArrayList<IProperty> findAllByOwner(@Param("ownerId") String ownerId);

    //查询
    @Select("SELECT* FROM i_property where status = 'active'  ")
    @Results({
            @Result(property="id",column="id"),
            @Result(property="agent",column="agentId",javaType=IUser.class,
                    one=@One(select="com.lance.shiro.mapper.UserMapper.get")),
            @Result(property="propertyList",column="propertyListId",javaType=IPropertyList.class,
                    one=@One(select="com.lance.shiro.mapper.PropertyListMapper.get")),
            @Result(property="lotType",column="lotTypeId",javaType=ILotType.class,
                    one=@One(select="com.lance.shiro.mapper.LotTypeMapper.get")),
            @Result(property="owner",column="ownerId",javaType=IUser.class,
                    one=@One(select="com.lance.shiro.mapper.UserMapper.get")),
    })
    ArrayList<IProperty> findAll();




}
