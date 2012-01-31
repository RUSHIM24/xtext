/*
* generated by Xtext
*/
package org.eclipse.xtend.ide.contentassist;

import static com.google.common.collect.Lists.*;
import static com.google.common.collect.Sets.*;

import java.util.List;
import java.util.Set;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.jdt.core.Flags;
import org.eclipse.jdt.core.search.IJavaSearchConstants;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.swt.graphics.Image;
import org.eclipse.xtend.core.xtend.XtendClass;
import org.eclipse.xtend.core.xtend.XtendField;
import org.eclipse.xtend.core.xtend.XtendPackage;
import org.eclipse.xtend.core.xtend.XtendParameter;
import org.eclipse.xtext.Assignment;
import org.eclipse.xtext.EcoreUtil2;
import org.eclipse.xtext.GrammarUtil;
import org.eclipse.xtext.IGrammarAccess;
import org.eclipse.xtext.RuleCall;
import org.eclipse.xtext.common.types.xtext.ui.ITypesProposalProvider;
import org.eclipse.xtext.common.types.xtext.ui.JdtVariableCompletions;
import org.eclipse.xtext.common.types.xtext.ui.JdtVariableCompletions.VariableType;
import org.eclipse.xtext.common.types.xtext.ui.TypeMatchFilters;
import org.eclipse.xtext.nodemodel.INode;
import org.eclipse.xtext.resource.IEObjectDescription;
import org.eclipse.xtext.ui.editor.contentassist.ConfigurableCompletionProposal;
import org.eclipse.xtext.ui.editor.contentassist.ContentAssistContext;
import org.eclipse.xtext.ui.editor.contentassist.ICompletionProposalAcceptor;
import org.eclipse.xtext.util.SimpleAttributeResolver;
import org.eclipse.xtext.xbase.XbasePackage;

import com.google.common.base.Predicate;
import com.google.inject.Inject;

/**
 * see http://www.eclipse.org/Xtext/documentation/latest/xtext.html#contentAssist on how to customize content assistant
 */
public class XtendProposalProvider extends AbstractXtendProposalProvider {

	@Inject
	private JdtVariableCompletions completions;

	@Inject
	private IGrammarAccess grammarAccess;

	@Inject
	private ImplementMemberFromSuperAssist overrideAssist;

	@Override
	public void completeImport_ImportedType(EObject model, Assignment assignment, ContentAssistContext context,
			ICompletionProposalAcceptor acceptor) {
		completeJavaTypes(context, XtendPackage.Literals.XTEND_IMPORT__IMPORTED_TYPE, true,
				getQualifiedNameValueConverter(), new TypeMatchFilters.All(IJavaSearchConstants.TYPE), acceptor);
	}

	@Override
	public void completeMember_Name(final EObject model, Assignment assignment, final ContentAssistContext context,
			final ICompletionProposalAcceptor acceptor) {
		if (model instanceof XtendField) {
			//TODO go up type hierarchy and collect all local fields
			final List<XtendField> siblings = EcoreUtil2.getSiblingsOfType(model, XtendField.class);
			Set<String> alreadyTaken = newHashSet(transform(siblings, SimpleAttributeResolver.NAME_RESOLVER));
			alreadyTaken.addAll(getAllKeywords());
			completions.getVariableProposals(model, XtendPackage.Literals.XTEND_FIELD__TYPE,
					VariableType.INSTANCE_FIELD, alreadyTaken, new JdtVariableCompletions.CompletionDataAcceptor() {
						public void accept(String replaceText, StyledString label, Image img) {
							acceptor.accept(createCompletionProposal(replaceText, label, img, context));
						}
					});
		} else {
			super.completeMember_Name(model, assignment, context, acceptor);
		}
	}

	protected Set<String> getAllKeywords() {
		return GrammarUtil.getAllKeywords(grammarAccess.getGrammar());
	}

	@Override
	public void completeParameter_Name(final EObject model, Assignment assignment, final ContentAssistContext context,
			final ICompletionProposalAcceptor acceptor) {
		if (model instanceof XtendParameter) {
			final List<XtendParameter> siblings = EcoreUtil2.getSiblingsOfType(model, XtendParameter.class);
			Set<String> alreadyTaken = newHashSet(transform(siblings, SimpleAttributeResolver.NAME_RESOLVER));
			alreadyTaken.addAll(getAllKeywords());
			completions.getVariableProposals(model, XtendPackage.Literals.XTEND_PARAMETER__PARAMETER_TYPE,
					VariableType.PARAMETER, alreadyTaken, new JdtVariableCompletions.CompletionDataAcceptor() {
						public void accept(String replaceText, StyledString label, Image img) {
							acceptor.accept(createCompletionProposal(replaceText, label, img, context));
						}
					});
		} else {
			super.completeParameter_Name(model, assignment, context, acceptor);
		}
	}

	@Override
	protected Predicate<IEObjectDescription> getFeatureDescriptionPredicate(ContentAssistContext contentAssistContext) {
		if (contentAssistContext.getPrefix().startsWith("_"))
			return super.getFeatureDescriptionPredicate(contentAssistContext);
		final Predicate<IEObjectDescription> delegate = super.getFeatureDescriptionPredicate(contentAssistContext);
		return new Predicate<IEObjectDescription>() {

			public boolean apply(IEObjectDescription input) {
				return !input.getName().getFirstSegment().startsWith("_") && delegate.apply(input);
			}

		};
	}

	@Override
	public void completeClass_Extends(EObject model, Assignment assignment, ContentAssistContext context,
			ICompletionProposalAcceptor acceptor) {
		completeJavaTypes(context, XbasePackage.Literals.XTYPE_LITERAL__TYPE, true, getQualifiedNameValueConverter(),
				new ITypesProposalProvider.Filter() {
					public int getSearchFor() {
						return IJavaSearchConstants.CLASS;
					}

					public boolean accept(int modifiers, char[] packageName, char[] simpleTypeName,
							char[][] enclosingTypeNames, String path) {
						if (TypeMatchFilters.isInternalClass(simpleTypeName, enclosingTypeNames))
							return false;
						return !Flags.isFinal(modifiers);
					}
				}, acceptor);
	}

	@Override
	public void completeClass_Implements(EObject model, Assignment assignment, ContentAssistContext context,
			ICompletionProposalAcceptor acceptor) {
		completeJavaTypes(context, XbasePackage.Literals.XTYPE_LITERAL__TYPE, true, getQualifiedNameValueConverter(),
				TypeMatchFilters.all(IJavaSearchConstants.INTERFACE), acceptor);
	}

	@Override
	public void completeClass_Members(EObject model, Assignment assignment, ContentAssistContext context,
			ICompletionProposalAcceptor acceptor) {
		if (model instanceof XtendClass)
			overrideAssist.createOverrideProposals((XtendClass) model, context, acceptor, getConflictHelper());
		super.completeClass_Members(model, assignment, context, acceptor);
	}

	protected void addGuillemotsProposal(ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
		acceptor.accept(new ConfigurableCompletionProposal("\u00AB\u00BB", context.getOffset(), context
				.getSelectedText().length(), 1));
	}

	public void completeInRichString(EObject model, RuleCall ruleCall, ContentAssistContext context,
			ICompletionProposalAcceptor acceptor) {
		INode node = context.getCurrentNode();
		int offset = node.getOffset();
		int length = node.getLength();
		String currentNodeText = node.getText();
		if (currentNodeText.startsWith("\u00BB") && offset + 1 <= context.getOffset()
				|| currentNodeText.startsWith("'''") && offset + 3 <= context.getOffset()) {
			if (context.getOffset() > offset && context.getOffset() < offset + length)
				addGuillemotsProposal(context, acceptor);
		} else if (currentNodeText.startsWith("\u00AB\u00AB")) {
			try {
				IDocument document = context.getViewer().getDocument();
				int nodeLine = document.getLineOfOffset(offset);
				int completionLine = document.getLineOfOffset(context.getOffset());
				if (completionLine > nodeLine) {
					addGuillemotsProposal(context, acceptor);
				}
			} catch (BadLocationException e) {
				// ignore
			}
		}
	}
	
	@Override
	public void completeXFeatureCall_Feature(EObject model, Assignment assignment, ContentAssistContext context,
			ICompletionProposalAcceptor acceptor) {
		if (model instanceof XtendField) {
			createLocalVariableAndImplicitProposals(model, context, acceptor);
		} else {
			super.completeXFeatureCall_Feature(model, assignment, context, acceptor);
		}
	}

	@Override
	public void complete_RICH_TEXT(EObject model, RuleCall ruleCall, ContentAssistContext context,
			ICompletionProposalAcceptor acceptor) {
		completeInRichString(model, ruleCall, context, acceptor);
	}

	@Override
	public void complete_RICH_TEXT_START(EObject model, RuleCall ruleCall, ContentAssistContext context,
			ICompletionProposalAcceptor acceptor) {
		completeInRichString(model, ruleCall, context, acceptor);
	}

	@Override
	public void complete_RICH_TEXT_END(EObject model, RuleCall ruleCall, ContentAssistContext context,
			ICompletionProposalAcceptor acceptor) {
		completeInRichString(model, ruleCall, context, acceptor);
	}

	@Override
	public void complete_RICH_TEXT_INBETWEEN(EObject model, RuleCall ruleCall, ContentAssistContext context,
			ICompletionProposalAcceptor acceptor) {
		completeInRichString(model, ruleCall, context, acceptor);
	}

	@Override
	public void complete_COMMENT_RICH_TEXT_END(EObject model, RuleCall ruleCall, ContentAssistContext context,
			ICompletionProposalAcceptor acceptor) {
		completeInRichString(model, ruleCall, context, acceptor);
	}

	@Override
	public void complete_COMMENT_RICH_TEXT_INBETWEEN(EObject model, RuleCall ruleCall, ContentAssistContext context,
			ICompletionProposalAcceptor acceptor) {
		completeInRichString(model, ruleCall, context, acceptor);
	}
}
