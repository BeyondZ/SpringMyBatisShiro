package com.lance.shiro.web;

import com.lance.shiro.entity.IContent;
import com.lance.shiro.entity.IUser;
import com.lance.shiro.service.ContentService;
import com.lance.shiro.service.UserService;
import com.lance.shiro.utils.UserStatus;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Map;

/**
 * Created by bingyun on 2018-06-05.
 */
@RestController
@RequestMapping("/rest/content")
public class ContentController extends BaseController {

    //注入contentService
    @Autowired
    private ContentService contentService;

    /**
     *add
     * @return
     */
    @RequestMapping(value = "", method = RequestMethod.POST)
    public ResponseEntity add(@RequestBody ArrayList<IContent> content) {
        ArrayList<Map> acontents = contentService.save(content);
        return success("Operation success!", acontents);
    }

    /**
     *update
     * @return
     */
    @RequestMapping(value = "", method = RequestMethod.PUT)
    public ResponseEntity update(@RequestBody  ArrayList<IContent> content) {
        ArrayList<Map> acontents = contentService.save(content);
        return success("Operation success!",acontents);
    }

    /**
     *query
     * @return
     */
    @RequestMapping(value = "", method = RequestMethod.GET)
    public ResponseEntity query(@RequestParam ArrayList<String> module) {
        ArrayList<Map> content = contentService.findByModule(module);
        return success("Operation success!",content);
    }
}
