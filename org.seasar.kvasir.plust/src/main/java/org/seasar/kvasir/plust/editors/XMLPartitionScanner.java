package org.seasar.kvasir.plust.editors;

import org.eclipse.jface.text.rules.IPredicateRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.MultiLineRule;
import org.eclipse.jface.text.rules.RuleBasedPartitionScanner;
import org.eclipse.jface.text.rules.Token;


public class XMLPartitionScanner extends RuleBasedPartitionScanner
{
    public final static String XML_DEFAULT = "__xml_default";

    public final static String XML_COMMENT = "__xml_comment";

    public final static String XML_TAG = "__xml_tag";


    public XMLPartitionScanner()
    {

        IToken xmlComment = new Token(XML_COMMENT);
        IToken tag = new Token(XML_TAG);

        IPredicateRule[] rules = new IPredicateRule[2];

        rules[0] = new MultiLineRule("<!--", "-->", xmlComment);
        rules[1] = new TagRule(tag);

        setPredicateRules(rules);
    }
}
