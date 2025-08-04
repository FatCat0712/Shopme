package com.shopme.admin.setting;

import com.shopme.admin.SupabaseS3Util;
import com.shopme.admin.currency.CurrencyRepository;
import com.shopme.common.entity.Currency;
import com.shopme.common.entity.setting.Setting;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static com.shopme.common.entity.Constants.SUPABASE_URI;

@Controller
public class SettingController {
    private final SettingService settingService;
    private final CurrencyRepository currencyRepository;

    @Autowired
    public SettingController(SettingService settingService, CurrencyRepository currencyRepository) {
        this.settingService = settingService;
        this.currencyRepository = currencyRepository;
    }

    @GetMapping("/settings")
    public String listAllSettings(Model model) {
        List<Setting> listSettings = settingService.listAllSettings();
        List<Currency> listCurrencies = currencyRepository.findAllByOrderByNameAsc();

        model.addAttribute("listCurrencies", listCurrencies);
        model.addAttribute("SUPABASE_URI", SUPABASE_URI);
        listSettings.forEach(setting -> model.addAttribute(setting.getKey(), setting.getValue()));

        return "settings/settings";
    }


    @PostMapping("/settings/save_general")
    public String saveGeneralSettings(@RequestParam("fileImage")MultipartFile multipartFile, HttpServletRequest request, RedirectAttributes redirectAttributes) throws IOException {
        GeneralSettingBag generalSettingBag = settingService.getGeneralSettingBag();
         saveSiteLogo(multipartFile, generalSettingBag);
         saveCurrencySymbol(request, generalSettingBag);
         updateSettingValueFromForm(request, generalSettingBag.list());

        redirectAttributes.addFlashAttribute("message", "General settings have been saved.");


        return "redirect:/settings#general";
    }

    @PostMapping("/settings/save_mail_server")
    public String saveMailServerSettings(HttpServletRequest request, RedirectAttributes ra) {
           List<Setting> mailServerSettings =  settingService.getMailServerSettings();
           updateSettingValueFromForm(request, mailServerSettings);
           ra.addFlashAttribute("message", "Mail server settings have been saved");
           return "redirect:/settings#mailServer";
    }

    @PostMapping("/settings/save_mail_templates")
    public String saveMailTemplatesSettings(HttpServletRequest request, RedirectAttributes ra) {
        List<Setting> mailTemplateSettings =  settingService.getMailTemplatesSettings();
        updateSettingValueFromForm(request, mailTemplateSettings);
        ra.addFlashAttribute("message", "Mail templates settings have been saved");
        return "redirect:/settings#mailTemplates";
    }

    @PostMapping("/settings/save_payment")
    public String savePaymentSettings(HttpServletRequest request, RedirectAttributes ra) {
        List<Setting> paymentSettings = settingService.getPaymentSettings();
        updateSettingValueFromForm(request, paymentSettings);
        ra.addFlashAttribute("message", "Payment settings have been saved");
        return "redirect:/settings#payment";
    }


    private void saveSiteLogo(MultipartFile multipartFile, GeneralSettingBag settingBag) throws IOException {
        if(!multipartFile.isEmpty()) {
            String fileName =  "/site-logo/" + StringUtils.cleanPath(Objects.requireNonNull(multipartFile.getOriginalFilename()));
            settingBag.updateSiteLogo(fileName);

            String uploadDir = "site-logo";
            SupabaseS3Util.removeFolder(uploadDir);
            SupabaseS3Util.uploadFile(uploadDir, fileName.replace("/site-logo/", ""), multipartFile.getInputStream());
        }
    }

    private void saveCurrencySymbol(HttpServletRequest request, GeneralSettingBag generalSettingBag) {
        Integer currencyId = Integer.parseInt(request.getParameter("CURRENCY_ID"));
        Optional<Currency> findByIdResult = currencyRepository.findById(currencyId);

        findByIdResult.ifPresent(currency -> {
            String symbol = findByIdResult.get().getSymbol();
            generalSettingBag.updateCurrencySymbol(symbol);
        });
    }

    private void updateSettingValueFromForm(HttpServletRequest request, List<Setting> listSettings) {
        for(Setting setting : listSettings) {
            String value = request.getParameter(setting.getKey());
            if( value != null) {
                setting.setValue(value);
            }
        }
        settingService.saveAll(listSettings);
    }


}
