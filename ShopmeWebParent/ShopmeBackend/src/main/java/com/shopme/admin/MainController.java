package com.shopme.admin;

import com.shopme.admin.article.ArticleService;
import com.shopme.admin.brand.BrandService;
import com.shopme.admin.category.CategoryService;
import com.shopme.admin.currency.CurrencyNotFoundException;
import com.shopme.admin.currency.CurrencyService;
import com.shopme.admin.customer.CustomerService;
import com.shopme.admin.menu.MenuService;
import com.shopme.admin.order.OrderService;
import com.shopme.admin.product.ProductService;
import com.shopme.admin.question.QuestionService;
import com.shopme.admin.review.ReviewService;
import com.shopme.admin.section.SectionService;
import com.shopme.admin.setting.GeneralSettingBag;
import com.shopme.admin.setting.SettingService;
import com.shopme.admin.setting.country.CountryService;
import com.shopme.admin.setting.state.StateService;
import com.shopme.admin.shippingrate.ShippingRateService;
import com.shopme.admin.user.UserService;
import com.shopme.common.entity.Currency;
import com.shopme.common.entity.setting.SettingBag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainController {
    private final SettingService settingService;
    private final CurrencyService currencyService;
    private final UserService userService;
    private final CategoryService categoryService;
    private final BrandService brandService;
    private final ProductService productService;
    private final QuestionService questionService;
    private final ReviewService reviewService;
    private final CustomerService customerService;
    private final ShippingRateService shippingRateService;
    private final OrderService orderService;
    private final ArticleService articleService;
    private final MenuService menuService;
    private final SectionService sectionService;
    private final CountryService countryService;
    private final StateService stateService;

    @Autowired
    public MainController(SettingService settingService, CurrencyService currencyService, UserService userService, CategoryService categoryService, BrandService brandService, ProductService productService, QuestionService questionService, ReviewService reviewService, CustomerService customerService, ShippingRateService shippingRateService, OrderService orderService, ArticleService articleService, MenuService menuService, SectionService sectionService, CountryService countryService, StateService stateService) {
        this.settingService = settingService;
        this.currencyService = currencyService;
        this.userService = userService;
        this.categoryService = categoryService;
        this.brandService = brandService;
        this.productService = productService;
        this.questionService = questionService;
        this.reviewService = reviewService;
        this.customerService = customerService;
        this.shippingRateService = shippingRateService;
        this.orderService = orderService;
        this.articleService = articleService;
        this.menuService = menuService;
        this.sectionService = sectionService;
        this.countryService = countryService;
        this.stateService = stateService;
    }

    @GetMapping("")
    public String viewHomePage(Model model) {
        try {
//            general information
            GeneralSettingBag generalSettingBag = settingService.getGeneralSettingBag();
            Currency  currency =currencyService.get(Integer.valueOf(generalSettingBag.get("CURRENCY_ID").getValue()));

//            users
            Integer numOfUsers = userService.listAll().size();
            Integer countEnabledUsers = userService.countEnabledUsers();
            Integer countDisabledUsers = userService.countDisabledUsers();

//            categories
            Integer numOfCategories = categoryService.listAll().size();
            Integer countRootCategories = categoryService.countRootCategories();
            Integer countEnabledCategories = categoryService.countEnabledCategories();
            Integer countDisabledCategories = categoryService.countDisabledCategories();

//            brands
            Integer numOfBrands = brandService.listAll().size();

//            products
            Integer numOfProducts = productService.listAll().size();
            Integer countEnabledProducts = productService.countEnabledProducts();
            Integer countDisabledProducts = productService.countDisabledProducts();
            Integer countInStockProducts  =productService.countInStockProducts();
            Integer countOutOfStockProducts = productService.countOutOfStockProducts();

//            questions
            Integer numOfQuestions = questionService.listAll().size();
            Integer countApprovedQuestions = questionService.countApprovedQuestions();
            Integer countUnapprovedQuestions = questionService.countUnapprovedQuestions();
            Integer countAnsweredQuestions = questionService.countAnsweredQuestions();
            Integer countUnansweredQuestions = questionService.countUnansweredQuestions();

//            reviews
            Integer numOfReviews = reviewService.listAll().size();
            Integer countOfReviewedProducts = reviewService.countReviewProducts();

//            customers
            Integer numOfCustomers = customerService.listAll().size();
            Integer countEnabledCustomers = customerService.countEnabledCustomers();
            Integer countDisabledCustomers = customerService.countDisabledCustomers();

//            shipping rates
            Integer numOfShippingRates = shippingRateService.listAll().size();
            Integer countCODSupported = shippingRateService.countCODSupported();

//            orders
            Integer numOfOrders = orderService.listAll().size();
            Integer countNewOrders = orderService.countNewOrders();
            Integer countDeliveredOrders = orderService.countDeliveredOrders();
            Integer countProcessingOrders = orderService.countProcessingOrders();
            Integer countShippingOrders = orderService.countShippingOrders();
            Integer countCancelledOrders = orderService.countCancelledOrders();

//            articles
            Integer numOfArticles = articleService.listAll().size();
            Integer countMenuBoundArticles = articleService.countMenuBoundArticles();
            Integer countFreeArticles = articleService.countFreeArticles();
            Integer countPublishedArticles = articleService.countPublishedArticles();
            Integer countUnpublishedArticles = articleService.countUnPublishedArticles();

//            menus
           Integer numOfMenus = menuService.listAll().size();
           Integer countHeaderMenus = menuService.countHeaderMenus();
           Integer countFooterMenus = menuService.countFooterMenus();
           Integer countEnabledMenus = menuService.countEnabledMenus();
           Integer countDisabledMenus = menuService.countDisabledMenus();

//           sections
            Integer numOfSections = sectionService.listAll().size();
            Integer countEnabledSections = sectionService.countEnabledSections();
            Integer countDisabledSections = sectionService.countDisabledSections();

//            settings
            Integer numOfCountries = countryService.listAll().size();
            Integer numOfStates = stateService.listAll().size();
            SettingBag mailSettingBag = settingService.getMailServerSettingsBag();


            model.addAttribute("generalSettingBag", generalSettingBag);
            model.addAttribute("currency", currency);

            model.addAttribute("numOfUsers", numOfUsers);
            model.addAttribute("countEnabledUsers", countEnabledUsers);
            model.addAttribute("countDisabledUsers", countDisabledUsers);

            model.addAttribute("numOfCategories", numOfCategories);
            model.addAttribute("countRootCategories", countRootCategories);
            model.addAttribute("countEnabledCategories", countEnabledCategories);
            model.addAttribute("countDisabledCategories", countDisabledCategories);

            model.addAttribute("numOfBrands", numOfBrands);

            model.addAttribute("numOfProducts", numOfProducts);
            model.addAttribute("countEnabledProducts", countEnabledProducts);
            model.addAttribute("countDisabledProducts", countDisabledProducts);
            model.addAttribute("countInStockProducts", countInStockProducts);
            model.addAttribute("countOutOfStockProducts", countOutOfStockProducts);

            model.addAttribute("numOfQuestions", numOfQuestions);
            model.addAttribute("countApprovedQuestions", countApprovedQuestions);
            model.addAttribute("countUnapprovedQuestions", countUnapprovedQuestions);
            model.addAttribute("countAnsweredQuestions", countAnsweredQuestions);
            model.addAttribute("countUnansweredQuestions", countUnansweredQuestions);

            model.addAttribute("numOfReviews", numOfReviews);
            model.addAttribute("countOfReviewedProducts", countOfReviewedProducts);

            model.addAttribute("numOfCustomers", numOfCustomers);
            model.addAttribute("countEnabledCustomers", countEnabledCustomers);
            model.addAttribute("countDisabledCustomers", countDisabledCustomers);

            model.addAttribute("numOfShippingRates", numOfShippingRates);
            model.addAttribute("countCODSupported", countCODSupported);

            model.addAttribute("numOfOrders", numOfOrders);
            model.addAttribute("countNewOrders", countNewOrders);
            model.addAttribute("countDeliveredOrders", countDeliveredOrders);
            model.addAttribute("countProcessingOrders", countProcessingOrders);
            model.addAttribute("countShippingOrders", countShippingOrders);
            model.addAttribute("countCancelledOrders", countCancelledOrders);

            model.addAttribute("numOfArticles", numOfArticles);
            model.addAttribute("countMenuBoundArticles", countMenuBoundArticles);
            model.addAttribute("countFreeArticles", countFreeArticles);
            model.addAttribute("countPublishedArticles", countPublishedArticles);
            model.addAttribute("countUnpublishedArticles", countUnpublishedArticles);

            model.addAttribute("numOfMenus", numOfMenus);
            model.addAttribute("countHeaderMenus", countHeaderMenus);
            model.addAttribute("countFooterMenus", countFooterMenus);
            model.addAttribute("countEnabledMenus", countEnabledMenus);
            model.addAttribute("countDisabledMenus", countDisabledMenus);

            model.addAttribute("numOfSections", numOfSections);
            model.addAttribute("countEnabledSections", countEnabledSections);
            model.addAttribute("countDisabledSections", countDisabledSections);

            model.addAttribute("numOfCountries", numOfCountries);
            model.addAttribute("numOfStates", numOfStates);
            model.addAttribute("mailSettingBag", mailSettingBag);



            return "index";
        } catch (CurrencyNotFoundException e) {
            return "error/500";
        }
    }

    @GetMapping("/login")
    public String viewLoginPage() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication == null || authentication instanceof AnonymousAuthenticationToken) {
            return "login";
        }
        return "redirect:/";
    }

}
