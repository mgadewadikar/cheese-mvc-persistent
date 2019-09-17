package org.launchcode.controllers;

import org.launchcode.models.Cheese;
import org.launchcode.models.Menu;
import org.launchcode.models.data.CheeseDao;
import org.launchcode.models.forms.AddMenuItemForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.launchcode.models.data.MenuDao;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.validation.Valid;

@Controller
@RequestMapping(value = "menu")
public class MenuController {

    @Autowired
    private MenuDao menuDao;

    @Autowired
    private CheeseDao cheeseDao;

    @RequestMapping(value = "")
    public String index(Model model) {

        model.addAttribute("title", "Menus");
        model.addAttribute("menus", menuDao.findAll());

        return "/menu/index";
    }

    @RequestMapping(value = "add", method = RequestMethod.GET)
    public String displayAddMenuForm(Model model) {

        model.addAttribute("title", "Add menu");

        // call the default constructor to create new empty menus
        // to help render the form
        model.addAttribute("menu", new Menu());

        return "menu/add";
    }

    // process data on the server
    @RequestMapping(value = "add", method = RequestMethod.POST)
    public String processAddMenuForm(Model model, @ModelAttribute @Valid  Menu menu, Errors errors) {

        if (errors.hasErrors()) {
            return "menu/add";
        }


        menuDao.save(menu);
        return "redirect:view/" + menu.getId();
    }


    @RequestMapping(value = "view/{menuId}", method = RequestMethod.GET)

    // allow the user to view the contents of a menu
    // we linked each menu to a URL above in POST request section
    public String viewMenu(Model model, @PathVariable int menuId) {

        // retrieve the Menu object with the given ID using menuDao
        Menu menu = menuDao.findOne(menuId);
        // display page title which is name of the menu
        model.addAttribute("title", menu.getName());
        model.addAttribute("cheeses", menu.getCheeses());
        // pass in the view the menu object
        model.addAttribute("menu", menu);
        return "menu/view";

    }

    @RequestMapping(value = "add-item/{menuId}", method = RequestMethod.GET)
    public String addItem(Model model, @PathVariable int menuId) {


        Menu menu = menuDao.findOne(menuId);

        AddMenuItemForm form = new AddMenuItemForm(cheeseDao.findAll(), menu);
        // render the form using form attribute
        model.addAttribute("form", form);
        model.addAttribute("title", "Add item to menu: " + menu.getName());
        return "menu/add-item";
    }


    @RequestMapping(value = "add-item", method = RequestMethod.POST)
    public String addItem(Model model, @ModelAttribute @Valid AddMenuItemForm form, Errors errors) {

        if (errors.hasErrors()) {
            model.addAttribute("form", form);

            // get the name by: first form.getMenuId() and then the name from menuDao using that Id
            model.addAttribute("title", "Add item to menu: " + menuDao.findOne(form.getMenuId()).getName());
            return "menu/add-item";
        }

        Cheese thecheese = cheeseDao.findOne(form.getCheeseId());
        Menu theMenu = menuDao.findOne(form.getMenuId());

        theMenu.addItem(thecheese);
        menuDao.save(theMenu);
        return "redirect:/menu/view/" + theMenu.getId();
    }


}
