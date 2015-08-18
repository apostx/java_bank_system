package wup.helper.view;

import java.util.StringJoiner;

public class FormatString {
    
    
    /**
     * Create a formatted Account Number string from source unformatted Account Number
     * @param source Unformatted Account Number
     * @return Formatted Account Number
     */
    public static String accountNumber(String source) throws IllegalArgumentException {
        final int SECTION_LENGTH = 8;
        final int SECTION_NUM = 3;
        
        if (source.length() != SECTION_NUM * SECTION_LENGTH)
            throw new IllegalArgumentException();
        
        StringJoiner result = new StringJoiner("-");
        
        for (int i = 0;i < SECTION_NUM; ++i)
            result.add(source.substring(i*SECTION_LENGTH,(i+1)*SECTION_LENGTH));

        return result.toString();
    }
}
