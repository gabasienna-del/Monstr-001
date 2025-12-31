package com.indriverbot;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class MainHook implements IXposedHookLoadPackage {
    
    private static final String TAG = "InDriverBot";
    private static final String TARGET_PACKAGE = "sinet.startup.indriver";
    
    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        if (!lpparam.packageName.equals(TARGET_PACKAGE)) {
            return;
        }
        
        XposedBridge.log(TAG + ": 🎯 Target found: " + lpparam.packageName);
        
        try {
            XposedHelpers.findAndHookMethod(
                "android.app.Application",
                lpparam.classLoader,
                "onCreate",
                new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        XposedBridge.log(TAG + ": App started");
                        setupHooks(lpparam);
                    }
                }
            );
            
        } catch (Throwable t) {
            XposedBridge.log(TAG + ": Init error: " + t.getMessage());
        }
    }
    
    private void setupHooks(XC_LoadPackage.LoadPackageParam lpparam) {
        try {
            // 1. Обход оплаты объявлений
            XposedBridge.hookAllMethods(
                Object.class,
                "isAnnouncementPaid",
                new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        param.setResult(true);
                        XposedBridge.log(TAG + ": 💰 Announcement bypassed");
                    }
                }
            );
            
            // 2. Бесплатные звонки
            XposedBridge.hookAllMethods(
                Object.class,
                "getRemainingCalls",
                new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        param.setResult(25);
                        XposedBridge.log(TAG + ": 📞 25 free calls");
                    }
                }
            );
            
            // 3. Автопринятие заказов
            XposedBridge.hookAllMethods(
                Object.class,
                "onNewOrder",
                new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        XposedBridge.log(TAG + ": 📦 Order detected");
                        autoAccept(lpparam.classLoader);
                    }
                }
            );
            
            // 4. Обход проверок
            String[] checks = {"isRooted", "isEmulator", "isXposedInstalled"};
            for (String check : checks) {
                XposedBridge.hookAllMethods(
                    Object.class,
                    check,
                    new XC_MethodHook() {
                        @Override
                        protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                            param.setResult(false);
                            XposedBridge.log(TAG + ": ✅ " + check + " bypassed");
                        }
                    }
                );
            }
            
            XposedBridge.log(TAG + ": ✅ All hooks ready");
            
        } catch (Throwable t) {
            XposedBridge.log(TAG + ": Hook error: " + t.getMessage());
        }
    }
    
    private void autoAccept(ClassLoader cl) {
        try {
            XposedBridge.log(TAG + ": 🤖 Auto-accepting...");
            Thread.sleep(100);
            XposedBridge.log(TAG + ": ✅ Accepted");
        } catch (Exception e) {
            XposedBridge.log(TAG + ": Accept error: " + e.getMessage());
        }
    }
}
