package core.project.library.domain.value_objects;

import jakarta.validation.constraints.NotBlank;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public record Phone(@NotBlank String phoneNumber) {

    public Phone {
        if (phoneNumber == null) {
            throw new IllegalArgumentException("Phone number cannot be null");
        }
        if (phoneNumber.isBlank()) {
            throw new IllegalArgumentException("Phone number should`t be blank.");
        }

//        String comprehensiveRegex = "^(?:(?:\\+?1\\s*(?:[.-]\\s*)?)?(?:\\(\\s*([2-9]1[02-9]|[2-9][02-8]1|[2-9][02-8][02-9])\\s*\\)|([2-9]1[02-9]|[2-9][02-8]1|[2-9][02-8][02-9]))\\s*(?:[.-]\\s*)?)?([2-9]1[02-9]|[2-9][02-9]1|[2-9][02-9]{2})\\s*(?:[.-]\\s*)?([0-9]{4})(?:\\s*(?:#|x\\.?|ext\\.?|extension)\\s*(\\d+))?$";
//
//        String patterns
//                = "^(\\+\\d{1,3}( )?)?((\\(\\d{3}\\))|\\d{3})[- .]?\\d{3}[- .]?\\d{4}$"
//                + "|^(\\+\\d{1,3}( )?)?(\\d{3}[ ]?){2}\\d{3}$"
//                + "|^(\\+\\d{1,3}( )?)?(\\d{2}[ ]?)(\\d{3}[ ]?)(\\d{2}){2}$"
//                + "|^(\\+\\d{1,3}( )?)?(\\d{3}[ ]?)(\\d{2}[ ]?){2}\\d{2}$";

//        String AZpattern = "^(\\+\\d{1,3}( )(\\d{2}[- ]?)(\\d{3}[- ]?)(\\d{2}[- ]?)(\\d{2}))$";
//
//        Pattern pattern = Pattern.compile(AZpattern);
//        Matcher matcher = pattern.matcher(phoneNumber);
//        if (!matcher.matches()) {
//            throw new IllegalArgumentException("Invalid phone number.\n Phone number: " + phoneNumber);
//        }
    }
}
