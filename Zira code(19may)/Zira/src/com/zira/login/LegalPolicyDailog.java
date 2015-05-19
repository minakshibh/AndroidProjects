package com.zira.login;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.twentyfourseven.zira.LoginActivity;
import com.twentyfourseven.zira.R;
import com.twentyfourseven.zira.VehicleSearchActivity;

public class LegalPolicyDailog extends Activity{
 Button btn_accept,btn_reject;
 String legal_policy;
	public void onCreate(Bundle savedInstanceState){
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.notificationdailog );

		
		
legal_policy  = "Legal content for riders:\n"
		+" These Terms of Use (the \"Terms\") govern the access or use by you, an individual, from within the United States " +
		" and its territories and possessions of applications, websites, content, products, and services (the \"Services\") " +
		" made available in the United States and its territories and possessions by Company X"
		+" and its subsidiaries and affiliates (here in after collectively referred to as X). PLEASE READ THESE TERMS " +
		" CAREFULLY BEFORE ACCESSING OR USING THE SERVICES. You are entering into a binding legal contract with X by " +
		" accepting the services, and the terms of that contract are state d here in. You must read, agree to, and accept " +
		" all of the terms and conditions stated herein and incorporated here in by reference. If you do not agree to any ofthese terms, please do not use the"+
		" services of X.Your access and use of the Services constitutes your agreement to be bound by these Terms,"+
		" which establishes a contractual relationship between you and X. If you do not agree to these"+
		" Terms, you may not access or use the Services. X may immediately terminate these Terms or any"+
		" Services with respect to you, or generally cease offering or deny access to the Services or any"+
		" portion there of, at any time for any reason. Supplemental terms may apply to certain Services,"+
		" such as policies for a particular event, activity or promotion, and such supplemental terms will"+
		" be disclosed to you in connection with the applicable Services. Supplemental terms are in addition"+
		" to, and shall be deemed a part of , the Terms for the purposes of the applicable Services. Supplemental"+
		" terms shall prevail over these Terms in the event of a conflict with respect to the applicable Services."+
		" X may amend the Terms related to the Services from time to time. Amendments will be effective upon"+
		" X's posting of such updated Terms at this location or the amended policies or supplemental "+
		" terms on the applicable Service. Your continued access or use of the Services after such posting "+
		" constitutes your consent to be bound by the Terms, as amended. X is not a transportation provider and"+
		" does not provide transportation services. The Services constitute a technology platform that enables users of" +
		" X’s mobile applications or websites provided as part of the Services to arrange and schedule transportation " +
		" and/or logistics services with third party providers of such services, including independent third party " +
		" transportation providers and third party logistics providers under agreement with X(\"Third Party Providers\")." +
		" Unless otherwise agreed by X in a separate written agreement with you, the Services are made available " +
		" solely for your personal, noncommercial use. \"YOU ACKNOWLEDGE THAT X DOES NOT PROVIDE TRANSPORTATION OR " +
		" LOGISTICS SERVICES OR FUNCTION AS A TRANSPORTATION CARRIER. X'S SERVICES MAY BE USED BY YOU TO REQUEST " +
		" AND SCHEDULE TRANSPORTATION OR LOGISTICS SERVICES WITH THIRD PARTY PROVIDERS, BUT YOU AGREE THAT X HAS NO " +
		" RESPONSIBILITY OR LIABILITY TO YO U RELATED TO ANY TRANSPORTATION OR GISTICS PROVIDED TO YOU BY THIRD PARTY" +
		"  PROVIDERS THROUGH THE USE OF THE SERVICES OTHER THAN AS EXPRESSLY SET FORTH IN THESE TERMS.X DOES NOT GUARANTEE " +
		" THE SUITABILITY, SAFETY OR ABILITY OF THIRD PARTY PROVIDERS. IT IS SOLELY YOUR RESPONSIBILITY TO DETERMINE IF ATHIRD" +
		" PARTY PROVIDER WILL MEET YOUR NEEDS AND EXPECTATIONS. X WILL NOT PARTICIPATE IN DISPUTES BETWEEN YOU AND A " +
		" THIRD PARTY PROVIDER. BY USING THE SERVICES, YOU ACKNOWLEDGE THAT YOU MAY BE EXPOSED TO SITUATIONS INVOLVING " +
		" THIRD PARTY PROVIDERS THAT ARE POTENTIALLY UNSAFE, OFFENSIVE, HARMFUL TO MINORS, OR OTHERWISE OBJECTIONABLE,AND " +
		" THAT USE OF THIRD PARTY PROVIDERS ARRANGED OR SCHEDULED USING THE SERVICES IS AT YOUR OWN RISK AND JUDGMENT. " +
		" UBER SHALL NOT HAVE ANY LIABILITY ARISING FROM OR IN ANY WAY RELATED TO YOUR TRANSACTIONS OR RELATIONSHIP WITH " +
		" THIRD PARTY PROVIDERS\". It is left to the discretion of the third party provider whether to provide transportation " +
		" services, and left to the YOUR discretion whether the third party provider is safe and meets your needs. LICENSE" +
		" Subject to your compliance with these Terms,X grants you a limited, non-exclusive, non-sublicensable, revocable, " +
		" non-transferrable license to: \n(i) access and use the Applications on your personal device solely in connection" +
		" with your use of the Services; and \n(ii) access and use any content, information and related materials that may " +
		" be made available through the Services, in each case solely for your personal, noncommercial use. Any rights not " +
		" expresslygranted hereinare reserved by X and X's licensors.RESTRICTIONSYou may not: \n(i) remove any copyright," +
		" trademark orother proprietary notices from any portion of the Services; \n(ii) reproduce, modify, prepare " +
		" derivative works basedupon, distribute, license,lease, sell, resell, transfer, publicly display, publicly " +
		" perform, transmit, stream, broadcast or otherwise exploit the Services except as expressly permitted by X;\n(iii) " +
		" decompile, reverseengineer or disassemble the Services except as may be permitted by applicable law; \n(iv) " +
		" link to,mirror or frame any portion of the Services;\n(v) cause or launch any programs or scripts for the" +
		" purpose of scraping, indexing, surveying, or otherwise data mining any portion of the Services orunduly burdening" +
		" or hindering the operation and/or functionality of any aspect of the Services; or \n(vi) attempt to gain unauthorized" +
		" access to or impair any aspect of the Services or its related systems or networks.THIRD-PARTY SERVICES AND " +
		" CONTENT The Services may be made available or accessed in connection with third-party services and content" +
		" (\"including advertising\") that X does not control. You acknowledge that different terms of use and privacy " +
		" policies may apply to your use of such third-party services and content.X does not endorse such third party " +
		" services and content and in no event shall X be responsible or liablefor any products or services of such " +
		" third party providers. Additionally, Apple Inc., Google, Inc., MicrosoftCorporation or BlackBerry Limited will " +
		" be third-party beneficiaries to this contract if you access the Services using Applications developed for Apple" +
		" iOS, Android, Microsoft Windows, or Blackberry-powered mobile devices, respectively. These third-party " +
		" beneficiaries are not parties to this contract and are not responsible for the provision or support of the" +
		" Services in any manner. Your access to theServices using these devices is subject to terms set forth in the " +
		" applicable third-party beneficiary's terms of service. OWNERSHIP The Services and all rights there in are and" +
		" shall remain X's property or the property of X's licensors. Neither these Terms nor your use of the Services" +
		" convey or grant to you any rights:  \n(i) in or related to the Services except for the limited license granted " +
		" above; or \n(ii) to use or reference in any manner X's company names, logos, product and service names, trade marks " +
		" or services marks or those of X's licensor. The Service is not available for use by persons under the age of 18." +
		" You may not authorize third  parties to use your Account, and you may not allow persons under the age of 18 to " +
		" receive transportation or logistics services from Third Party Providers unless they are accompanied by you. You " +
		" may not assign or otherwise transfer your Account to any other person or entity. You agree to comply with all " +
		" applicable laws when using the Services, and you may only use the Services for lawful purposes (e.g., no transport" +
		" of unlawful or hazardous materials). You will notin your use of the Services cause nuisance, annoyance, " +
		" inconvenience, or property damage, whether to the Third Party Provider or any other party. In certain instances," +
		" X may require you to provide proof of identity to access or use the Services, and you agree that you may be" +
		" denied access or use of the Services if you refuse to provide proof of identity. You are responsible for " +
		" obtaining the data network access necessary to use the Services. Your mobile network's data and messaging" +
		" rates and fees may apply if you access or use the Services from a wireless-enabled device. You are responsible " +
		" for acquiring and updating compatible hardware or devices necessary to access and use the Services and " +
		" Applications and any updates there to. X does not guarantee that the Services, or any portion thereof, will " +
		" function on any particular hardware or devices. In addition, the Services may be subject to malfunctions and " +
		" delays inherent in the use of the Internet and electronic communications.You understand that use of " +
		" the Services may result in payments by you for the services you receive from a Third Party Provider (Charges). " +
		" After you have received services obtained through your use of the Service,X will facilitate payment of" +
		" the applicable Charges on be half of the Third Party Provider, as such Third Party Provider's limited" +
		" payment collection agent, using the preferred payment method designated in your account,and will send" +
		" you a receipt by email. Payment of the Charges in such manner shall be considered the same as payment" +
		" made directly by you to the Third Party Provider.Charges will be inclusive of applicable taxes where " +
		" required by law. Charges paid by you are final and non-refundable, unless otherwise determined by X.You" +
		" retain the right to request lower charges from a Third Party Provider for services received by you from such " +
		" Third Party Provider at the timeyou receive such services. X will respond accordingly to any request from a Third " +
		" Party Provider to modify the charges for a particular service. All charges are due immediately and payment will be " +
		" facilitated by X using the preferred payment method designated in your Account. If your primary Account payment " +
		" method is determined to be expired, invalidor otherwise not able to be charged, you agree that X may, as the " +
		" Third Party Provider's limited payment collection agent, use a secondary payment method in your Account, if " +
		" available. X reserves the right to establish, remove and/or revise Charges for any or all aspects ofthe Services " +
		" at any time in X's sole discretion. Further, you acknowledge and agree that Charges applicable in certain " +
		" geographical areas may increase substantially during times of high demand of the Services. X will use" +
		" reasonable efforts to inform you of Charges that may apply, provided that you will be responsible for" +
		" Charges incurred under your Account regardless of your awareness of such Charges or the amounts there of.You" +
		" may electto cancel your request for Services from a Third Party Provider at any time prior to such ThirdParty " +
		" Provider's arrival,in which case you may be charged a cancellation fee.This payment structure is intended to " +
		" fully compensate the Third Party Provider for the services provided. Except with respect to taxi cab transportation " +
		" services requested through the Application, Xdoes not designate any portion of your paymentas a tip or gratuity to " +
		" the Third Party Provider. You understand and agree that,while you are free to provide additional paymentas a " +
		" gratuity to any Third Party Provider who provides you with services obtained through the Service, you are under " +
		" no obligation to do so. Gratuities are voluntary. REPAIR OR CLEANING FEES You shall be responsible for the cost " +
		" of repair for damage to, or necessary cleaning of, ThirdParty Provider vehicles and property resulting from your" +
		" use of the Services in excess of normal wear and tear damages and necessary cleaning (Repair or Cleaning). " +
		" In the event thata Third Party Provider reports the need for Repair or Cleaning, and such Repair or" +
		" Cleaning request is verified by X in X's reasonable discretion, X reserves the right to facilitate payment " +
		" for the reasonable cost of such Repair or Cleaning on behalf of the Third Party Provider using your preferred " +
		" payment method designated in your Account. Such amounts will be transferred by Xtothe applicable Third Party" +
		" Provider and are non-refundable.THE SERVICES ARE PROVIDED AS IS AND AS AVAILABLE. XDISCLAIMS ALL REPRESENTATIONS" +
		" AND WARRANTIES, EXPRESS, IMPLIED, OR STATUTORY, NOTEXPRESSLY SET OUT IN THESE TERMS, INCLUDING THE IMPLIED " +
		" WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NON-INFRINGEMENT. IN ADDITION, XMAKES NO" +
		" REPRESENTATION, WARRANTY, ORGUARANTEE REGARDING THE RELIABILITY, TIMELINESS, QUALITY,SUITABILITY, OR " +
		" AVAILABILITY OF THE SERVICES OR ANY GOODS OR SERVICES OBTAINED THROUGH THE USE OF THE SERVICES, OR THAT THE " +
		" SERVICES WILL BE UNINTERRUPTED OR ERROR-FREE. YOU AGREE THAT THE ENTIRE RISKARISING OUT OF YOUR USE OF THE " +
		" SERVICES, AND ANY THIRD PARTY GOOD ORSERVICES OBTAINED IN CONNECTION THEREWITH, REMAINS SOLELY WITH YOU,TO " +
		" THE MAXIMUM EXTENT PERMITTED BY APPLICABLE LAW. THIS DISCLAIMER DOES NOT ALTER YOUR RIGHTS AS A CONSUMER TO " +
		" THEEXTENT NOT PERMITTED UNDER THE LAW IN THE JURISDICTION OF YOURPLACE OF RESIDENCE. X SHALL NOT BE LIABLE TO " +
		" YOU FOR INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, PUNITIVE, OR CONSEQUENTIAL DAMAGES, INCLUDING LOST PROFITS, " +
		" LOST DATA, PERSONAL INJURY, OR PROPERTY DAMAGE, EVEN IFXHASBEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGES." +
		" XSHALL NOT BELIABLE FOR ANY DAMAGES, LIABILITY OR LOSSES INCURRED BY YOU ARISINGOUT OF:\n(i) YOUR USE OF OR" +
		" RELIANCE ON THE SERVICES OR YOUR INABILITYTO ACCESS OR USE THE SERVICES; OR \n (ii) ANY TRANSACTION OR " +
		" RELATIONSHIP BETWEEN YOU AND ANY THIRD PARTY PROVIDER, EVEN IF XHAS BEENADVISED OF THE POSSIBILITY OF " +
		" SUCH DAMAGES. X SHALL NOT BE LIABLE FORDELAY OR FAILURE IN PERFORMANCE RESULTING FROM CAUSES BEYOND X'" +
		" SREASONABLE CONTROL.IN NO EVENT SHALL X'S TOTAL LIABILITY TO YOU INCONNECTION WITH THE SERVICES FOR ALL " +
		" DAMAGES, LOSSES AND CAUSES OFACTION EXCEED FIVE HUNDRED U.S. DOLLARS (US $500).THESE LIMITATIONS DO NOT" +
		" PURPORT TO LIMIT LIABILITY THAT CANNOT BEEXCLUDED UNDER THE LAW IN THE JURISDICTION OF YOUR PLACE OF RESIDENCE." +
		" INDEMNITY You agree to indemnify and hold X and its officers, directors, employees and agents, harmless from any " +
		" and all claims, demands, losses, liabilities, and expenses, including attorneys' fees, arising out of or in " +
		" connection with:\n(i) your use of the Services; \n(ii) your breach or violation ofany of these Terms; " +
		"\n(iii) X's use of your user content; or \n(iv) your violation of the rights of any third party, including " +
		" Third Party Providers. ARBITRATION You agree that any dispute, claim or controversy arising out of or relating to these" +
		" Terms or thebreach, termination, enforcement, interpretation or validity there of or the use of the Services" +
		" will be settled by binding arbitration between you and X, except that each party retains the right " +
		" tobring an individual action in small claims court and the right to seek injunctive or other equitablerelief " +
		" in a court of competent jurisdiction to prevent the actual or threatened infringement, misappropriation or" +
		" violation of a party's copyrights, trademarks, trade secrets, patents or other intellectual property rights." +
		" You acknowledge and agree that you andXare each waiving the right to a trial by jury or to participate" +
		" as a plaintiff or class in any purported class action or representative proceeding. Further, unless both you " +
		" and X otherwise agree in writing, the arbitrator may not consolidate more than one person's claims, and may not" +
		" otherwise preside over any form of any class or representative proceeding. Except as provided in the " +
		" preceding sentence, this Dispute Resolution section will survive any termination of these Terms. ARBITRATION " +
		" RULES AND GOVERNING LAW The arbitration will be administered by the American Arbitration Association " +
		" (AAA) in accordance with the Commercial Arbitration Rules and the Supplementary Procedures for " +
		" Consumer Related Disputes (the AAA Rules) then in effect, except as modified by this Dispute " +
		" Resolution section. (The AAA Rules are available at www.adr.org/arb_med or by calling the AAA at" +
		" 1-800-778-7879.) The Federal Arbitration Act will govern the interpretation and enforcement of this Section."+
		" ARBITRATION PROCESS A party who desires to initiate arbitration" +
		" must provide the other party with a written Demand for Arbitration as specified in the AAA Rules. " +
		" (The AAA provides a form Demand for Arbitration at www.adr.org/aaa/ShowPDF?doc= ADRSTG_004175 and a " +
		" separate form for California residents at www.adr.org/aaa/ShowPDF?doc= ADRSTG_015822"+".) " +
		" The arbitrator will be either a retired judge or an attorney licensed to practice law in " +
		" the state of California and will be selected by the parties from the AAA's roster of consumer " +
		" dispute arbitrators. If the parties are unable to agree upon an arbitrator within fourteen " +
		"(14) days of delivery of the Demand for Arbitration, then the AAA will appoint the arbitrator" +
		" in accordance with the AAA Rules. ARBITRATION LOCATION AND PROCEDURE Unless you and "+
		" otherwise agree, the arbitration will be conducted in the county where you reside. If your claim does not" +
		" exceed $10,000, then the arbitration will be conducted solely on the basis of documents you and X submit to" +
		" the arbitrator, unless you request a hearing or the arbitrator determines that a hearing is necessary. If your" +
		" claim exceeds"+
		" $10,000, your right to a hearing will be determined by the AAA Rules. Subject to the AAA Rules, the arbitrator will have"+
		" the discretion to direct a reasonable exchange of information by the parties, consistent with the expedited nature " +
		" of the arbitration. ARBITRATOR'S DECISION The arbitrator will render an award within the time frame specified in the AAA Rules. " 
		+" The arbitrator's decision will include the essential findings and conclusions upon which the arbitrator based the award. Judgment"+
		" on the arbitration award may be entered in any court having jurisdiction there of. The arbitrator's award damages must be consistent with"
		+" the terms of the Limitation of Liability section above as to the types and the amounts of damages for which a party may be held liable."
		+" The arbitrator may award declaratory or injunctive relief only in favor of the claimant and only to the extent necessary " +
		"to provide relief warranted by the claimant's individual claim. If you prevail in arbitration you will be entitled to an award of attorneys' "+
		" fees and expenses, to the extent provided under applicable law. FEES Your responsibility to pay any AAA filing, administrative and arbitrator"+
		" fees will be solely as set forth in the AAA Rules. CHANGES Not with standing the provisions of the modification-related provisions above, if " +
		" Company changes this Dispute Resolution section after the date you first accepted the se Terms (or accepted any subsequent changes to these Terms), you may"+
		" reject any such change by providing x written notice of such rejection by mail or hand delivery, within 30 days of the date such"+
		" change became effective. In order to be effective, the notice must include your full name and clearly indicate your intent to reject changes to this Dispute Resolution"+
		" section. By rejecting changes, you are agreeing that you will arbitrate any Dispute between you and X in accordance"+
		" with the provisions of this Dispute Resolution section as of the date you first accepted these Terms (or accepted any subsequent changes to these Terms)."+
		" CHOICE OF LAW These Terms are governed by and construed in accordance with the laws of the State of California, U.S.A., without"+
		" giving effect to any conflict of law principles. X may assign this contract, without your consent,to:\n(i) a subsidiary or affiliate; \n(ii) an acquirer"+
		" of X's equity, business or assets; or \n(iii) a successor by merger. Any purported assignment in violation of this section shall be void. No joint"+
		" venture, partnership, employment, or agency relationship exists between you, X or any Third Party Provider as a result of this Agreement or"+
		" use of the Services. If any provision of these Terms is held to be invalid or unenforceable, such provision shall be struck and the remaining provisions"+
		" shall be enforced to the fullest extent under law. Our failure to enforce any right or provision in these Terms shall not constitute awaiver " +
		"of such right or provision unless acknowledged and agreed to by X in writing. IN ADDITION TO THE ABOVE, DRIVERS SHOULD ALSO AGREE TO THE FOLLOWING"+
		":By using the services of X, a driver represents, warrants and agrees that:Such driver is at least 21 years of age.Such driver possesses a valid driver's " +
		" license and is authorized to operate a motor vehicle and has all appropriate licenses, approvals and authority to provide transportation"+
		" to third parties in all jurisdictions in which such driver uses X’s services. Such driver owns,or has the legal right to operate, the "+
		" vehicle such driver uses when accepting riders, and such vehicle is in good operating condition and meets the"+
		" industry safety standards and all applicable statutory and state department of motor vehicle requirements for"+
		" a vehicle of its kind. Such driver is named or scheduled on the insurance policy covering the vehicle"+
		" such driver uses when accepting riders. Such driver has a valid policy of liability insurance (in coverage amounts"+
		" consistent with all applicable legal requirements) for the operation of such Driver's vehicle to cover any anticipated"+
		" losses related to such driver's provision of rides to riders. Such driver will be solely responsible for"+
		" any and all liability which results from or is alleged as a result of the operation of the vehicle"+
		" such Driver uses to transport Riders, including, but not limited to personal injuries, death and property damages."+
		" In the event of a motor vehicle accident such driver will be solely responsible forcompliance with any applicable"+
		" statutory or department of motor vehicles requirements, and for all necessary contacts with such driver's"+ 
		" insurance carrier. Such driver will obey all local laws related to the matters set forth here in, and will"+
		" be solely responsible for any violations of such local laws. Such driver will not make any misrepresentation "+
		" regarding X or services provided by X or such driver's status as a driver, offer or provide transportation"+
		" service for profit,as a public carrier or taxi service, charge for rides or otherwise seek"+
		" non-voluntary compensation from riders, or engage in any other activity in a manner that is inconsistent"+
		" with such driver's obligations under this Agreement. Such driver will not discriminate or harass anyone on"+
		" the basis of race, national origin, religion, gender, gender identity, physical or mental disability, medical"+
		" condition, marital status, age or sexual orientation. Such driver is medically fit to drive in accordance with"+
		" applicable law.                    \n        \n                \n                 \n         ";
		
		setFinishOnTouchOutside(false);
		initialiseVariable();
		setListeners();
}
	
	private void initialiseVariable() {
	btn_accept=(Button)findViewById(R.id.button_accept);
	btn_reject=(Button)findViewById(R.id.button_reject);
	TextView tv_legelpolicy=(TextView)findViewById(R.id.textView_text);
	tv_legelpolicy.setText(legal_policy);
	
		
	}
	
	private void setListeners() {
		btn_accept.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
			Intent i=	new Intent(LegalPolicyDailog.this,VehicleSearchActivity.class);
			i.putExtra("splitfare", "yes");
			startActivity(i);
			
				finish();
				
			}
		});
		
		btn_reject.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
			startActivity(new Intent(LegalPolicyDailog.this,LoginActivity.class));
			finish();
				
			}
		});
		
	}
}