package com.teioh.m_feed.UI.ReaderActivity.Presenters;

import android.os.Bundle;

import com.teioh.m_feed.MangaEnums;
import com.teioh.m_feed.Models.Chapter;
import com.teioh.m_feed.UI.ReaderActivity.IReader;
import com.teioh.m_feed.Utils.MangaLogger;

import static com.teioh.m_feed.UI.ReaderActivity.Presenters.ChapterMangaPresenter.CHAPTER_PARENT_FOLLOWING;
import static com.teioh.m_feed.UI.ReaderActivity.Presenters.ChapterMangaPresenter.CHAPTER_POSITION_LIST_PARCELABLE_KEY;


public class ChapterNovelPresenter implements IReader.NovelFragmentPresenter
{
    public final static String TAG = ChapterNovelPresenter.class.getSimpleName();

    private boolean mIsToolbarShowing, mChapterParentFollowing;
    private int mPosition;
    private Chapter mChapter;
    private MangaEnums.eLoadingStatus mLoadingStatus;

    private IReader.NovelFragmentView mChapterReaderMapper;

    /***
     * ChapterNovelPresenter Constructor
     * @param aMap
     */
    public ChapterNovelPresenter(IReader.NovelFragmentView aMap, Bundle aBundle)
    {
        mChapterReaderMapper = aMap;
        mPosition = aBundle.getInt(CHAPTER_POSITION_LIST_PARCELABLE_KEY);
        mChapter = aBundle.getParcelable(Chapter.TAG + ":" + mPosition);
        mChapterParentFollowing = aBundle.getBoolean(CHAPTER_PARENT_FOLLOWING, false);
        mIsToolbarShowing = true;
        mLoadingStatus = MangaEnums.eLoadingStatus.LOADING;

    }

    @Override
    public void init(Bundle aBundle)
    {
        mChapterReaderMapper.setContentText(mTestText); //set textview content
        mChapterReaderMapper.setUserGestureListener();

    }


    String mTestText = "The town of Wushan. An ordinary little town located within the Kingdom of Fenlai, west of the Mountain Range of Magical Beasts, the largest mountain range within the Yulan continent.\n" +
            "\n" +
            "As the morning sun rose, in the town of Wushan, there remained a slight hint of the cold, pure pre-dawn air. However, virtually all of the citizens of this small town had already come out to begin working. Even the six or seven year old children had already gotten out of bed and were preparing to begin their traditional morning exercises.\n" +
            "\n" +
            "On an empty area in the eastern region of Wushan town, the warmth of the rays of the morning sun passed through the surrounding trees, leaving behind scattered spots of light on the empty ground.\n" +
            "\n" +
            "A large group of children could be seen there, approximately one or two hundred in number. These children were separated into three groups, each group divided into several lines. All the children stood there silently, their faces solemn. The northernmost group of children were approximately six years old. The group in the middle, approximately nine to twelve years old. The ones in the south, the thirteen to sixteen year olds.\n" +
            "\n" +
            "In front of this large group of children, there were three sturdily-built middle-aged men. All three of them wore short-sleeved shirts and roughly cut trousers.\n" +
            "\n" +
            "“If you want to be a powerful warrior, then you must work hard from youth.” The leader of the middle-aged men, head raised high, hands clasped behind his back, said to them coldly. He swept his cold, fierce gaze across the northernmost group of children. All of those six and seven year olds tightened their lips. Gazing at this man with their big, round eyes, none of them dared to make a sound.\n" +
            "\n" +
            "The leader’s name was Hillman (Xi’er’man). He was the captain of the guard for the Baruch (Ba’lu’ke) clan, the noble clan which owned Wushan town.\n" +
            "\n" +
            "“All of you are commoners. Unlike those noble families, you won’t have access to any secret manuals teaching you how to cultivate battle qi [dou qi]. If you want to become someone of worth, if you wish to be respected, then all of you must use the most ancient, most simple, and most basic ways of improving yourselves – through exercising your bodies, and building up your strength! Am I clear?!”\n" +
            "\n" +
            "Hillman swept the group of children with his gaze.\n" +
            "\n" +
            "“Understood.” The voices of the children replied brightly in unison.\n" +
            "\n" +
            "“Good.” Satisfied, Hillman coldly nodded. The eyes of the six year old children displayed their confusion, while the eyes of the teenagers became very determined. They understood the meaning behind Hillman’s words.\n" +
            "\n" +
            "Virtually every male in the Yulan continent would exercise very hard from a very young age. If anyone slacked off, in the future, they would be looked down upon by others! Money and power – these were the things that determined a man’s status! A man without power would be looked down upon even by women.\n" +
            "\n" +
            "If one wanted their parents to be proud of them, wanted women to worship them, wanted to live a glorious life?\n" +
            "\n" +
            "Then they must become powerful warriors!\n" +
            "\n" +
            "All of them were commoners. None of them would have access to any of those precious manuals which taught the arts of cultivating battle qi. Their only road to glory was through exercising from a young age, and gathering strength! Bitterly hard work! They would work harder than those nobles, spend more of their energy and blood in strengthening themselves!\n" +
            "\n" +
            "“When the sun rises in the morning, all things begin to thrive. This is the best time to absorb the natural energy from your surroundings and improve the conditioning of our bodies. Same rules as always – Legs spread apart, as wide as your shoulders! Both knees bent slightly, both hands pressed down at the waist. Assume the ‘Qi Building Stance’. When assuming this stance, remember – ‘Focus your concentration, maintain a calm mind, and breath naturally.’” Hillman coldly instructed.\n" +
            "\n" +
            "The ‘Qi Building Stance’ was the most simple, yet most effective way of exercising one’s body. This was based off of the experiences of generations of forefathers.\n" +
            "\n" +
            "Immediately the nearly two hundred children assumed the ‘Qi Building Stance’ position.\n" +
            "\n" +
            "“Remember, focus your concentration, maintain a calm mind, and breath naturally!” Hillman said coldly as he walked amidst the children.\n" +
            "\n" +
            "At a glance, he could tell that the teenagers in the southern group all were maintaining the stance calmly and breathed naturally. At the same time, all of them attained the goals of being stable and steady in their stance. Clearly, they had attained some degree of proficiency in the ‘Qi Building Stance.’\n" +
            "\n" +
            "But glancing at the northernmost group of children, with their waists and knees crooked at odd degrees, their legs relaxed and loose, it was clear to Hillman that they were standing unstably and without any power.\n" +
            "\n" +
            "Hillman said to the two other middle aged men, “The two of you, take charge of the south group and the middle group. I will go take care of the youngest children.”\n" +
            "\n" +
            "“Yes, Captain.” The two middle aged men immediately obeyed, paying close attention to those two groups. Every so often, they would kick the legs of those teenagers, checking to see who was standing firmly and who was not.\n" +
            "\n" +
            "Hillman walked towards the northern group of children. Those children immediately became nervous.\n" +
            "\n" +
            "“Crap, the Head Monster is coming!” A golden-haired child with large, bright eyes named Hadley (Ha’de’li) said in a low voice.\n" +
            "\n" +
            "Hillman strode into the midst of the children. Staring at them, his face was cold, but in his heart, he was sighing. “These kids are just too young. They are just too lacking in both wisdom and strength. I can’t demand too much from them. However, it’s still good to get them exercising from a young age. If they work hard from a young age, in the future, when they are on the battlefield, they will have a higher chance of survival.”\n" +
            "\n" +
            "And to teach young children…getting them interested was the most effective way! If he forced them too hard, it would have the opposite effect!\n" +
            "\n" +
            "“All of you, stand firm!” Hillman coldly harrumphed.\n" +
            "\n" +
            "Immediately, all of the children straightened, sticking out their chests and staring straight ahead.\n" +
            "\n" +
            "A hint of a smile played at Hillman’s lips. He then moved to the front and took off his shirt. The lines running across the powerful muscles on his body made the eyes pop out of all of the kids. Even the children in the middle group and southern group couldn’t help but stare at him, admiring his physique.\n" +
            "\n" +
            "Aside from his perfect musculature, on Hillman’s bare upper body, there were countless knife scars, sword scars, and dozens of other old wounds. All of the children stared at those wounds, their eyes shining.\n" +
            "\n" +
            "Knife scars. Sword scars. These were a man’s medals!\n" +
            "\n" +
            "In their hearts, they were filled with veneration towards Hillman. Hillman, a mighty warrior of the sixth rank, a warrior birthed from life and death struggles! Even in large cities, he would be an amazing individual. In the tiny town of Wushan, he was a man who every single person venerated.\n" +
            "\n" +
            "Seeing the ardent gazes of the children, Hillman couldn’t help but let a slight smile escape. He wanted to stir up a feeling of worshipfulness in the children, a desire to be like him. That way, they would work harder and be more motivated!\n" +
            "\n" +
            "“Let’s add some more fuel to the fire!” Hillman secretly grinned, then walked in front of a giant boulder, which weighed three or four hundred pounds.\n" +
            "\n" +
            "With one hand, Hillman grabbed the boulder. In a very relaxed manner, he began brandishing it about. That three hundred pound boulder, in Hillman’s hands, seemed to be as light as wood. All of those children’s jaws dropped, and their eyes widened.\n" +
            "\n" +
            "“Too light! Lorry (Luo’rui), if you have some free time after training, go and get some larger boulders for me.” With a casual toss, Hillman sent the boulder flying several dozen meters. Crash! It smote the ground next to a large tree, and the entire ground trembled. Hillman casually walked in front of some random stones.\n" +
            "\n" +
            "“Hah!”\n" +
            "\n" +
            "Hillman breathed deeply. All of the veins on his muscular body popped out prominently, as Hillman directly struck at a nearby bluish boulder. His fist shattered the air, creating a howling sound that made all of the watching children widen their eyes even further. Hillman’s mighty fist smashed directly onto the boulder.\n" +
            "\n" +
            "Crash! The sound of the fist smashing into the boulder made the hearts of all the children tremble.\n" +
            "\n" +
            "That was an extremely hard bluestone boulder!\n" +
            "\n" +
            "The bluestone boulder trembled. Suddenly, six or seven giant cracks appeared on it, as with a ‘peng’ sound, it split into four or five pieces. But on Hillman’s fist, not the slightest injury could be seen.\n" +
            "\n" +
            "“The Captain is as formidable as ever.” Lorry, one of the two other middle-aged men, laughed, as Hillman walked back towards them.\n" +
            "\n" +
            "The other man, Roger (Luo’jie), also walked over. Usually, when the children practiced the ‘Qi Building Stance’, it was time for the three of them to relax and freely chat, while paying attention to any child who decided to slack off.\n" +
            "\n" +
            "Hillman laughed as he shook his head. “No way. In the past, when I was in the army, every day I would train like crazy, while on the battlefield, I engage in bloody close combat. Nowadays, all I’m doing is relaxing and stretching my muscles a bit in the morning. I’m not filled with as much energy as in the past.”\n" +
            "\n" +
            "All of the children stared worshipfully at Hillman.\n" +
            "\n" +
            "That huge bluestone boulder was shattered by a single blow from his fist. What sort of power was this? And that three or four hundred pound boulder was so easily tossed with a flick of the arm. What sort of power was this?\n" +
            "\n" +
            "Hillman turned his head. Staring at the children, he was very satisfied with the children’s reactions.\n" +
            "\n" +
            "“Remember, even if you aren’t able to cultivate battle qi, in principle, if you reach your body’s fullest potential, you can still become a warrior of the sixth rank! And a sixth ranked warrior, upon entering the army, can easily become a mid-level officer, and easily obtain the military manuals which teach one how to cultivate battle qi! Even if you cannot become a warrior of the sixth rank, and can only become a common warrior of the first rank, you will still be qualified to enter the military. Remember! If a man isn’t able to become even a warrior of the first rank, that man can’t be considered a man at all!”\n" +
            "\n" +
            "“If you are a man, then you must raise your chest high, welcome any and all challenges, and fear nothing!”\n" +
            "\n" +
            "Upon hearing these words, smiles appeared on the faces of all the six and seven year olds. All of them forced themselves to remain expressionless. These words were Hillman’s often-repeated mantra, and he repeated these words endlessly to the children.\n" +
            "\n" +
            "“All of you, stand straight. Look at your elders to the south, then look at how you are standing!” Hillman censured them.\n" +
            "\n" +
            "All of the six year olds immediately tried to adjust their stance to be more stable.\n" +
            "\n" +
            "After a while, the six and seven year olds began to wobble. All of the kids felt that their legs were cramping fiercely, but they gritted their teeth. But after holding out for a short period of time, the children began to collapse and sit on the ground, one after the other.\n" +
            "\n" +
            "Hillman’s face was cold and callous, but in his heart, he secretly nodded. He was still very satisfied with the performance of these six and seven year olds.\n" +
            "\n" +
            "After a short period of time, some of the ten year olds in the middle group also could no longer hold out, and one by one, they began to fall as well.\n" +
            "\n" +
            "“Hold out for as long as you can. I won’t force you. But if in the future, you are weaker than your peers, then you’ll have no one to blame than yourselves.” Hillman coldly said.\n" +
            "\n" +
            "“Hmm?” Lorry suddenly stared, astonished, at the northern group.\n" +
            "\n" +
            "At this point in time, many of the kids in the middle group had fallen down, but in the northern group, a six year old child had held strong.\n" +
            "\n" +
            "“This must be Linley’s [Lin’lei] first day at training. Who would’ve thought he’d be so formidable?” Lorry said, amazed. Next time him, Roger and Hillman also noticed. Looking in that direction, they saw that to the north, a single brown haired boy was still holding firm. His lips tightened, the boy stared determinedly in front, both fists tightly clenched so hard that his fists were white.\n" +
            "\n" +
            "A look of pleased surprise appeared in Hillman’s eyes.\n" +
            "\n" +
            "“Good kid!” Hillman secretly praised. Despite being just six years old, he could maintain the ‘Qi Building Stance’ for as long as the ten year old kids.\n" +
            "\n" +
            "Linley, full name Linley Baruch, was the eldest son and heir to the Baruch clan, which ruled over the Wushan town. The Baruch clan was an extremely old clan. Once, it was extremely prosperous, but after thousands of years, it had only three members remaining. The clan leader, Hogg [Huo’ge] Baruch, and his two sons. The elder son was Linley Baruch, six years old. The younger son, Wharton [Wo’dun] Baruch, was just two years old. As for his wife, when she gave birth to the younger son, she died in the midst of childbirth. Linley’s grandfather also was dead, having lost his life in battle.\n" +
            "\n" +
            "Linley’s legs were trembling. Although his willpower was strong, his leg muscles were strained to their utmost and were beginning to tremble uncontrollably. He finally collapsed and sat down.\n" +
            "\n" +
            "“Linley, how do you feel?” Smiling, Hillman walked towards him.\n" +
            "\n" +
            "Linley cracked a smile, revealing his small canines. “I’m fine, Uncle Hillman.” As captain of the Baruch clan’s guardsmen, Hillman had watched Linley grow up. Naturally, the two of them were very close.\n" +
            "\n" +
            "“Well done. You acted like a man.” Hillman patted Linley on the head. Immediately, the hair on Linley’s head became tousled like windblown grass.\n" +
            "\n" +
            "“Haha.” Linley grinned widely. In his heart, he felt very happy at being praised by Hillman.\n" +
            "\n" +
            "After resting for a while, they continued their exercises. The training regime for the six and seven year olds was a lot more relaxed. But for the teenagers, the training regime was terrifyingly strict.\n" +
            "\n" +
            "The large group of children, including the six and seven year olds, were lying down with their heads and their feet each on top of a flat rock, relying solely on the strength in their waists to keep straight.\n" +
            "\n" +
            "“The waist and the thighs form a triangular region.” Hillman gestured with his hands to show the area he was describing. “This area is a person’s nucleus. Speed and power all come from this triangular nucleus, making this region extremely important.”\n" +
            "\n" +
            "As Hillman spoke, he continued to walk about, carefully inspecting the youths to see if their movements were correct.\n" +
            "\n" +
            "“Tighten that up! Your waists need to be straight!” Hillman thundered.\n" +
            "\n" +
            "Immediately, the waists of many youths straightened. This was Linley’s first day of training. His tiny head and his feet were both flat on the rocks, but by this point in time, Linley could already feel his waist growing tight and hot.\n" +
            "\n" +
            "“Hold, gotta hold. I’m the best!” Linley kept encouraging himself. Linley’s body had always been very strong, even as a baby. He had virtually never gotten sick. Given that he also worked very hard, for him to excel was nothing special.\n" +
            "\n" +
            "“Thud!” The first child fell down.\n" +
            "\n" +
            "However, the stones they were using as a pillow and footrest were only twenty centimeters high, so although the child fell down, it didn’t hurt much. (In the Yulan continent, the goldsmiths used standardized lengths of 1 meter = 10 decimeters = 100 centimeters = 1000 millimeters.)\n" +
            "\n" +
            "“Thud!” “Thud!” As time went on, more and more children could no longer hold out.\n" +
            "\n" +
            "Linley gritted his teeth. He could clearly feel that the tightness in his waist had already reached the limits of his endurance, to the point where it was almost going numb. “My body feels so heavy. I’m almost unable to control it. Hold, gotta hold for just a bit more.” By this point in time, of the six to eight year olds, only Linley remained.\n" +
            "\n" +
            "Staring at Linley, Hillman couldn’t help but be filled with surprise and joy.\n" +
            "\n" +
            "“Lorry.” Hillman suddenly shouted.\n" +
            "\n" +
            "“Captain.” Lorry immediately straightened, awaiting his orders.\n" +
            "\n" +
            "Hillman commanded, “Tomorrow, prepare some special dyes. When they are practicing their waist strength, put a branch under all of their waists, and dye the branches. If any of them slack off and let their waists touch the branch, their body will be dyed as well. Their training regime will double in difficulty.”\n" +
            "\n" +
            "“Yes, Captain.” Lorry acknowledged the order. He couldn’t help but let his lips tug up in a smile. He secretly laughed, “The Captain is always filled with so many devilish ideas. Those punks are really gonna get it now.”\n" +
            "\n" +
            "Wasn’t that just so?\n" +
            "\n" +
            "Looks of pain appeared on the faces of all the ten year olds. Normally, they could still make slight adjustments and slack off. But with Hillman’s idea, they would have no chance to do so.\n" +
            "\n" +
            "Hillman continued coldly, “Let me tell you all, when a warrior practices his battle qi, the battle qi is stored in a fist-sized location directly beneath the navel. You should understand that this is part of the triangle I was talking about. I expect you all now should understand the importance of strengthening the triangle region! This is your core. If it fails, then your body fails, no matter how strong the other parts of it might be.”\n" +
            "\n" +
            "A good instructor is of paramount importance to the children.\n" +
            "\n" +
            "And Hillman really was a formidable warrior. He knew the important parts of training, and also knew how to increase the difficulty one step at a time. He knew what sort of tools to use with what ages. If it was too hard, it could make a child’s body collapse.\n" +
            "\n" +
            "“Battle qi?”\n" +
            "\n" +
            "Upon hearing these words, all of the youths, including the youngest children resting off to the side, stared at Hillman with wide eyes.\n" +
            "\n" +
            "All of the commoners were extremely eager to learn battle qi. Even Linley, this scion of a noble house, was extremely eager.\n" +
            "\n" +
            "“Thud!”\n" +
            "\n" +
            "Linley could finally hold out no longer, but he still used his arms to prop himself on the ground as he slowly rolled off.\n" +
            "\n" +
            "“That feels good!” Linley could feel that his waist felt a numbness which pierced through to the bone, so comfortable that his eyes crinkled slightly.\n" +
            "\n" +
            "“How long was I able to hold out?” Linley opened his eyes wide, looking around him.\n" +
            "\n" +
            "All of the six year olds had collapsed. Even half the ten year olds had collapsed as well. All of the fourteen year olds, however, held on. Hillman’s face remained as cold as ever.\n" +
            "\n" +
            "“All of you must remember. Your body is like a vessel, like a wineglass. Battle qi is like the wine! The amount of wine a vessel can hold is dependent on the size of the vessel. Same goes for the body; a person’s ability to practice battle qi is based on the extent of his training. If his body is too weak, even if he gains access to powerful battle qi manuals, his body won’t be able to hold much battle qi, and he still won’t become a powerful warrior.” Hillman imparted many important bits of advice to the children.\n" +
            "\n" +
            "Many warriors, due to not having received proper guidance in their youth, only understood the connection between battle qi and body strength much later in life. But by that age, there wouldn’t be much progress when they trained.\n" +
            "\n" +
            "Many forefathers had gone on many wrong paths and gained much experience. Hillman continued to impart these experiences, like the spring wind imparting life-giving rain, deeply etching these important experiences in the minds of these children. Hillman didn’t want these children to go on wrong paths as well.\n" +
            "\n" +
            "After practicing the ‘Qi Building Stance’, the waist, back, thighs, shoulders, and other parts of the body would be harmonized. Now, almost all of the children were sitting, relaxed, on the ground. Hillman’s training program was nearly perfect in the difficulty levels he assessed on each age group.\n" +
            "\n" +
            "“Today’s training ends now,” Hillman announced.\n" +
            "\n" +
            "Wushan town’s training regimes were regulated. Every day, it happened twice, once at dawn, and once at dusk.\n" +
            "\n" +
            "“Uncle Hillman, tell us some stories!” As training ended, the children immediately began to call out. Every day, after the dawn lessons, Hillman would tell them stories of his army days, or some events which had happened on the continent.\n" +
            "\n" +
            "The children, all of whom had lived in the town their entire lives, thirsted for stories about the military.\n" +
            "\n" +
            "Hillman smiled. He enjoyed telling stories to the children. This was a way to make the kids eager to train. Hillman had always felt that only by making the children voluntarily train would the children have great results.\n" +
            "\n" +
            "“Today, I will tell you about the legendary Four Supreme Warrior bloodlines which everyone in the continent knows about.” A look of awe appeared on Hillman’s face.\n" +
            "\n" +
            "The children’s ears immediately perked up, and their eyes brightened. Linley, sitting on the ground, felt his heart thump furiously. “The legendary Four Supreme Warriors?” Linley’s ears couldn’t help but perk up as well, as he stared unblinkingly at Hillman.\n" +
            "\n" +
            "In Hillman’s eyes appeared a hint of excitement. His voice, however, remained calm. “On our continent, thousands of years ago, there appeared four powerful Supreme Warriors. All four of these Supreme Warriors possessed power comparable to an enormous dragon. They could wander amidst an army of millions at leisure, and easily take the head of any general! These Supreme Warriors were known as the Dragonblood Warrior, the Violetflame Warrior, the Tigerstriped Warrior, and the Undying Warrior!”\n" +
            "\n" +
            "“Warriors are divided into nine ranks. I, a mere warrior of the sixth rank, can easily shatter boulders and kick down a large tree! But a ninth rank warrior, even within our country of Fenlai, would be considered a top level expert. But above the ninth rank warriors are the Four Supreme Warriors. They have surpassed the ninth rank warriors and can be considered the pinnacle of warriors. They belong to the level of legendary Saint-level warriors!” Hillman’s eyes were filled with excitement. “The legendary Saint-level warriors can melt giant icebergs, make the boundless sea roar with angry waves, make tall mountains crumble, make cities with millions of people collapse, and make meteors fall from the sky! They are absolutely undefeatable, the highest possible power.”\n" +
            "\n" +
            "Silence. All of the children were stunned.\n" +
            "\n" +
            "Hillman pointed at a mountain to the northeast.\n" +
            "\n" +
            "“Look at Wushan. Isn’t it huge?” Hillman smiled.\n" +
            "\n" +
            "After hearing Hillman’s words, many of the kids had been scared silly. They all immediately nodded. Wushan was over a thousand meters high, and thousands of meters in circumference. In the eyes of men, it would definitely be considered a huge mountain.\n" +
            "\n" +
            "“But Saint-level combatants can destroy Wushan in the blink of an eye.” Hillman said firmly.\n" +
            "\n" +
            "A sixth-ranked warrior can only smash a boulder. But a Saint-level warrior can smash an entire mountain! All of the children’s mouths dropped, and their eyes widened. All of them were shocked, and their hearts were suddenly filled with an unspeakable dread towards these Saint-level combatants. But, their hearts were also filled with longing.\n" +
            "\n" +
            "“Destroy a mountain?” Hillman’s words had a huge impact on Linley.\n" +
            "\n" +
            "After a short period of time, the stunned children returned to their homes. Hillman, Roger, and Lorry were the last to leave. Watching the children depart in clusters of three or five, a smile appeared on Hillman’s face.\n" +
            "\n" +
            "“These children are the hope and future of Wushan,” Hillman said with a smile.\n" +
            "\n" +
            "Roger and Lorry also gazed at the group of children. On the continent, virtually all of the children of commoners had to train hard from an early age. Seeing the kids, Roger and Lorry reminisced back to their own youth.\n" +
            "\n" +
            "“Captain Hillman, you are definitely much more formidable than ole Potter of bygone years. Under your guidance, I believe that Wushan town will become the strongest town in our region, surpassing the other ten or so towns,” Lorry said with a smile.\n" +
            "\n" +
            "The strength of a teacher determined a place’s future.\n" +
            "\n" +
            "“Oh, Captain, how do you know about the power of Saint-level warriors, or the Four Supreme Warriors?” Lorry suddenly remembered to ask.\n" +
            "\n" +
            "Slightly embarrassed, Hillman grinned, “Well, um, actually, I’m not too clear about exactly how powerful the Four Supreme Warriors are. After all, they are the stuff of legends. It’s been years since any were seen.”\n" +
            "\n" +
            "Lorry and Roger were astonished. “You don’t have any idea, and yet you lied to the kids?”\n" +
            "\n" +
            "Hillman smiled slightly. “Although I’m not clear about the exact strength of the Four Supreme Warriors, I know this – a Saint-level mage maestro, which is to say a mage which has attained the Saint-level, can execute forbidden magical techniques and eradicate an entire army of tens of thousands, or an entire city. Since Saint-level mages are so powerful, I expect that Saint-level warriors can’t be that much weaker.”\n" +
            "\n" +
            "“More importantly, the reason I told the children these stories was to make them work harder. Couldn’t you tell how amazed those children were after hearing the stories?” Hillman smiled delightedly.\n" +
            "\n" +
            "Lorry and Roger were both speechless.\n" +
            "\n" +
            "…..\n" +
            "\n" +
            "“See ya later, ‘Ley!”\n" +
            "\n" +
            "“See ya, Hadley!”\n" +
            "\n" +
            "Bidding farewell to his good friend Hadley, Linley went back, alone, to his home. After walking for a while, he saw the Baruch estates.\n" +
            "\n" +
            "The amount of land the Baruch manor was built upon was actually quite large. Moss was growing on the walls, and all sorts of ivy creepers twined up the walls as well. The scars of time were very apparent on the walls. The Baruch manor located in Wushan town was the ancestral home of the Baruch clan. An ancestral home which had existed for over five thousand years and endured countless renovations continued to stand here.\n" +
            "\n" +
            "But, with the decline in the clan’s fortunes, the Baruch clan’s finances had taken a turn for the worse as well. Towards the end, it could only consume its previous gains. Over a hundred years ago, the then-leader of the Baruch clan determined that all the members of the clan would live in the front courtyard, which took up a third of the space of the manor. The rest of the manor would no longer be maintained. That way, a great deal of money could be saved.\n" +
            "\n" +
            "But despite these measures, by this period in time, Linley’s father, Hogg Baruch, still needed to sell off family possessions in order to keep the family afloat.\n" +
            "\n" +
            "The towering doors to the manor were open.\n" +
            "\n" +
            "“Saint-level warriors?” While walking, Linley was still thinking about that. “In the future, will I be able to become a Saint-level warrior?”\n" +
            "\n" +
            "“Linley.” Hillman’s voice sounded from behind him. Hillman, Roger, and Lorry had finally caught up to him.\n" +
            "\n" +
            "Linley turned around and immediately said happily, “Uncle Hillman!”\n" +
            "\n" +
            "Following this, Linley sucked in a deep breath. Raising his head to look at Hillman, his voice filled with eagerness, he said, “Uncle Hillman, are Saint-level warriors really that powerful? Then what about me? Is it possible that I will become a Saint-level warrior?” In Linley’s heart, there was a desire which all children possess.\n" +
            "\n" +
            "Hillman was stunned. Besides him, Roger and Lorry were also speechless.\n" +
            "\n" +
            "A Saint-level warrior?\n" +
            "\n" +
            "“This kid really has the daring to dream big. The country of Fenlai has millions of citizens, but even so, after countless centuries, it hasn’t produced a single Saint-level warrior. To want to become a Saint-level warrior…” In Hillman’s mind, he fully understood how difficult it was to become a Saint-level warrior.\n" +
            "\n" +
            "It required someone to work extremely hard from a young age, the support of a noble clan, and also a high amount of natural talent. It also required luck. How could it be easy to become a Saint-level warrior?\n" +
            "\n" +
            "Hillman knew quite well how much he himself had to suffer in order to become a sixth-ranked warrior, and how many life-and-death battles he had to experience. Even a warrior of the sixth rank was very difficult to become. A seventh, eighth, and ninth ranked warrior was of course only harder. As for a Saint-level warrior? Even in his dreams, Hillman didn’t dare imagine himself as one.\n" +
            "\n" +
            "But he was facing Linley’s earnest gaze.\n" +
            "\n" +
            "“Linley, Uncle Hillman has faith in you. I’m sure you’ll become a Saint-level warrior.” Staring at Linley, Hillman spoke firmly. These words of encouragement caused Linley’s eyes to shine. In Linley’s heart, as well, a desire arose.\n" +
            "\n" +
            "A desire which had never been so ardent!\n" +
            "\n" +
            "“Uncle Hillman, from tomorrow onwards, can I participate in the training sessions with the ten year olds?” Linley suddenly asked.\n" +
            "\n" +
            "Hillman, Roger, and Lorry all stared at Linley in surprise.\n" +
            "\n" +
            "“My lord father always told me, if you want to become a man without peer, then you must work harder than other men.” Linley unconsciously mimicked his father’s manner of speech.\n" +
            "\n" +
            "Hillman suddenly smiled. He had seen the results of Linley’s training today. Although Linley was only six, his body conditioning could compare with nine year olds. He immediately nodded, smiling. “Fine. However, you’d best not slack off. You’d best realize that this isn’t a one day or two day commitment. This will be a long-term regime.”\n" +
            "\n" +
            "Linley raised his small head proudly. Self-confidently, he smiled. “Uncle Hillman, you just wait and see.”\n" +
            "\n" +
            "This was a very normal morning for Wushan town. Afterwards, every morning was the same as this one. The group of Wushan youths would follow Hillman, warrior of the sixth rank, and train hard under his guidance. The only difference was, the six year old Linley was placed in the central squad of ten year olds.";

    @Override
    public void onSaveState(Bundle aSave)
    {

    }

    @Override
    public void onRestoreState(Bundle aRestore)
    {

    }

    @Override
    public void onPause()
    {

    }

    @Override
    public void onResume()
    {

    }

    @Override
    public void onDestroy()
    {

    }

    @Override
    public void updateChapterViewStatus()
    {

    }

    @Override
    public void onRefresh(int aPosition)
    {

    }

    @Override
    public void toggleToolbar()
    {
        try
        {
            if (mIsToolbarShowing)
            {
                mIsToolbarShowing = false;
                mChapterReaderMapper.hideToolbar(0);
            }
            else
            {
                mIsToolbarShowing = true;
                mChapterReaderMapper.showToolbar();
            }
        }
        catch (Exception aException)
        {
            MangaLogger.logError(TAG, aException.getMessage());
        }
    }

    @Override
    public void updateReaderToolbar()
    {

    }
}
