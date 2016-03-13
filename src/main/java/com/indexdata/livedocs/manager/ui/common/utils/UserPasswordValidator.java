package com.indexdata.livedocs.manager.ui.common.utils;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.widgets.Shell;

import edu.vt.middleware.password.LengthRule;
import edu.vt.middleware.password.Password;
import edu.vt.middleware.password.PasswordData;
import edu.vt.middleware.password.PasswordValidator;
import edu.vt.middleware.password.Rule;
import edu.vt.middleware.password.RuleResult;
import edu.vt.middleware.password.WhitespaceRule;

/**
 * Validates input password.
 * 
 * @author Adam Dec
 * @since 0.0.4
 */
public class UserPasswordValidator {

    public static boolean validate(Shell shell, String password) {
        // password must be between 5 and 90 chars long
        LengthRule lengthRule = new LengthRule(5, 90);

        // don't allow whitespace
        WhitespaceRule whitespaceRule = new WhitespaceRule();

        List<Rule> ruleList = new ArrayList<Rule>(2);
        ruleList.add(lengthRule);
        ruleList.add(whitespaceRule);

        PasswordValidator validator = new PasswordValidator(ruleList);
        RuleResult result = validator.validate(new PasswordData(new Password(password)));
        if (!result.isValid()) {
            for (String msg : validator.getMessages(result)) {
                UIControlUtils.createWarningMessageBox(shell, msg);
            }
            return false;
        }
        return true;
    }
}