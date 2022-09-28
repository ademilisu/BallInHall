package com.ademilisu.ballinhall;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.ScreenUtils;

import java.util.Deque;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class BallinHall extends ApplicationAdapter {

	private AdsService adsService;
	Preferences preferences;
	static Status status = Status.READY;
	BitmapFont gameOverText;
	BitmapFont scoreText;
	BitmapFont highScoreText;
	SpriteBatch batch;
	Texture background;
	Texture redBlock;
	Texture trapBlock;
	Texture spikeBlock;
	Texture holeBlock;
	Texture myFont;
	BlockEffectUtil effectedBlock;
	Ball ball;
	Bonus bonus;
	Bonus enemy;
	Bonus sizeBonus;
	Meteor meteor;
	Meteor meteor2;
	Meteor meteor3;
	Timer gameTimer;
	Timer bonusTimer;
	float gravity;
	float tempMov;
	float totalWay;
	float tempMaxBallVelocity;
	float mov;
	float hMax;
	float blockWidth;
	float blockHeight;
	boolean isJumping = false;
	boolean isFalling = false;
	float floor = 0;
	float velocity = 0;
	boolean reverse;
	Block temp;
	Deque<Block> upBlocks;
	Deque<Block> downBlocks;
	Random random;
	float last;
	boolean isOver;
	boolean isCracked;
	boolean isBlockCreating;
	boolean holeActive;
	boolean level1;
	boolean level2;
	boolean level3;
	int bonusCount;
	int sizeBonusCount;
	int score = 0;
	int period;
	int bonusRandom;
	int enemyRandom;
	int highScore;
	int time;
	int adTime;
	boolean isAddingAdTime;
	float maxY;
	float maxX;
	float ballWidth;
	float bonusRadius;
	float meteorRadius;
	float maxBallVelocity;
	float totalHoleVelocity;
	private ParticleEffect sizeBonusEffect;
	private ParticleEffect blockEffect;
	private ParticleEffect trapEffect;

	public BallinHall(AdsService adsService) {
		this.adsService = adsService;
	}

	@Override
	public void create () {
		adTime = 0;
		preferences = Gdx.app.getPreferences("Preferences");
		ballWidth = maxY / 12;
		maxBallVelocity = 0;
		maxY = Gdx.graphics.getHeight();
		maxX = Gdx.graphics.getWidth();
		hMax = maxY / 2;
		random = new Random();
		blockHeight = maxY / 20;

		batch = new SpriteBatch();
		background = new Texture("background.jpg");
		trapBlock = new Texture("trap-block.png");
		redBlock = new Texture("red-block.png");
		spikeBlock = new Texture("spike-block.png");
		holeBlock = new Texture("hole.png");
		myFont = new Texture(Gdx.files.internal("myFont.png"));
		myFont.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);

		gameOverText = new BitmapFont(Gdx.files.internal("myFont.fnt"), new TextureRegion(myFont), false);
		gameOverText.getData().setScale(4);

		scoreText = new BitmapFont(Gdx.files.internal("myFont.fnt"), new TextureRegion(myFont), false);
		scoreText.getData().setScale(2);

		highScoreText = new BitmapFont(Gdx.files.internal("myFont.fnt"), new TextureRegion(myFont), false);

		ParticleEffect meteorEffect = new ParticleEffect();
		meteorEffect.load(Gdx.files.internal("meteor-flame.p"), Gdx.files.internal(""));

		ParticleEffect meteorEffect2 = new ParticleEffect();
		meteorEffect2.load(Gdx.files.internal("meteor-flame.p"), Gdx.files.internal(""));

		ParticleEffect meteorEffect3 = new ParticleEffect();
		meteorEffect3.load(Gdx.files.internal("meteor-flame.p"), Gdx.files.internal(""));

		meteor = new Meteor();
		meteor.setEffect(meteorEffect);

		meteor2 = new Meteor();
		meteor2.setEffect(meteorEffect2);

		meteor3 = new Meteor();
		meteor3.setEffect(meteorEffect3);

		ball = new Ball();
		ball.setExplode(new ParticleEffect());
		ball.getExplode().load(Gdx.files.internal("ball-explode-effect.p"), Gdx.files.internal(""));
		ball.setBallEffect(new ParticleEffect());
		ball.getBallEffect().load(Gdx.files.internal("ball-effect.p"), Gdx.files.internal(""));
		ball.setTexture(new Texture("ball.png"));

		sizeBonusEffect = new ParticleEffect();
		sizeBonusEffect.load(Gdx.files.internal("hole-effect.p"), Gdx.files.internal(""));

		blockEffect = new ParticleEffect();
		blockEffect.load(Gdx.files.internal("block-effect.p"), Gdx.files.internal(""));

		trapEffect = new ParticleEffect();
		trapEffect.load(Gdx.files.internal("trap-effect.p"), Gdx.files.internal(""));

		bonus = new Bonus();
		bonus.setTexture(new Texture("bonus.png"));
		bonus.setType("bonus");
		bonus.setCircle(new Circle());

		enemy = new Bonus();
		enemy.setTexture(new Texture("enemy.png"));
		enemy.setType("enemy");
		enemy.setCircle(new Circle());

		sizeBonus = new Bonus();
		sizeBonus.setTexture(new Texture("size.png"));

		effectedBlock = new BlockEffectUtil();

		gameCreate();
	}

	public void calculateGravity(int number) {
		gravity = hMax / number;
		totalWay -= ballWidth/2;
		while(tempMaxBallVelocity == 0) {
			tempMov += gravity;
			totalWay -= tempMov;
			if(totalWay < blockHeight) {
				tempMaxBallVelocity = tempMov + 2*gravity;
				totalWay = hMax;
				tempMov = 0;
				maxBallVelocity = tempMaxBallVelocity;
			}
		}
		tempMaxBallVelocity = 0;
	}

	public void gameCreate() {
		mov = 0;
		downBlocks = new LinkedList<>();
		upBlocks = new LinkedList<>();
		ballWidth = maxY / 12;
		blockWidth = maxY / 10;
		blockHeight = maxY / 20;
		bonusRadius = maxY / 32;
		meteorRadius = ballWidth/3;

		totalWay = hMax;
		tempMaxBallVelocity = 0;
		tempMov = 0;
		calculateGravity(5000);

		ball.setCircle(new Circle((maxX / 5) + ballWidth/2 , hMax, ballWidth/2));
		ball.getBallEffect().start();
		ball.setSize(BallSize.MID);
		ball.setBurst(false);
		ball.setEffectCount(0);

		createBonusPosition(bonus);
		bonus.getCircle().setRadius(bonusRadius/2);
		bonus.setVelocity(10*gravity);
		bonus.setSwell(false);
		bonus.setActive(false);

		createBonusPosition(enemy);
		enemy.getCircle().setRadius(bonusRadius/2);
		enemy.setVelocity(-10*gravity);
		enemy.setSwell(false);
		enemy.setActive(false);

		meteor.setCircle(new Circle( 2*maxX, (maxY - 3*blockHeight)*random.nextFloat() + 3*blockHeight/2, meteorRadius));
		meteor.setRatio(1700*gravity);
		meteor.setVelocity(-maxY/meteor.getRatio());
		meteor.setActive(false);

		meteor2.setCircle(new Circle( 3*maxX/2, (maxY - 3*blockHeight)*random.nextFloat() + 3*blockHeight/2, meteorRadius));
		meteor2.setRatio(1800*gravity);
		meteor2.setVelocity(-maxY/meteor2.getRatio());
		meteor2.setActive(false);

		meteor3.setCircle(new Circle( 4*maxX, (maxY - 3*blockHeight)*random.nextFloat() + 3*blockHeight/2, meteorRadius));
		meteor3.setRatio(1800*gravity);
		meteor3.setVelocity(-maxY/meteor3.getRatio());
		meteor3.setActive(false);

		sizeBonus.setCircle(new Circle(maxX + 3*ballWidth, maxY + 3*ballWidth, 0));

		sizeBonusEffect.start();
		meteor.getEffect().start();

		isCracked = false;
		isBlockCreating = false;
		isOver = false;
		holeActive = false;
		effectedBlock.setActive(false);
		effectedBlock.setCount(0);
		bonusCount = 0;
		bonusRandom = 11;
		enemyRandom = 2;
		sizeBonusCount = 0;
		totalHoleVelocity = 0;
		time = 0;
		velocity = 0;
		period = 10;
		highScore = preferences.getInteger("highScore");
		isAddingAdTime = false;

		level1 = false;
		level2 = false;
		level3 = false;

		for(int i=0; i<13; i++) {
			createNewBlock(i, upBlocks, "up");
		}

		for(int i=0; i<13; i++) {
			createNewBlock(i, downBlocks, "down");
		}

	}

	@Override
	public void render () {
		if(status.equals(Status.READY)) {
			if(Gdx.input.justTouched()) {
				status = Status.RUN;
				ballTimer();
				bonusAndEnemyTimer();
				reverse = false;
				isFalling = true;
				isJumping = false;
				isOver = false;
				score = 0;
			}
		}

		batch.begin();
		if(status.equals(Status.READY) || status.equals(Status.RUN)) {
			batch.draw(background, 0, 0, maxX, maxY);
			meteor.getEffect().update(Gdx.graphics.getDeltaTime());
			meteor2.getEffect().update(Gdx.graphics.getDeltaTime());
			meteor3.getEffect().update(Gdx.graphics.getDeltaTime());

			if(enemy.isActive()) {
				if (downBlocks.getLast().getRectangle().getX() > maxX) {
					isBlockCreating = true;
					last = downBlocks.getFirst().getRectangle().getX();
					downBlocks.addFirst(downBlocks.removeLast());
					downBlocks.getFirst().getRectangle().setX(last - 4 * blockWidth);
					if(downBlocks.getFirst().isHole()) {
						downBlocks.getFirst().getRectangle().setY(-blockHeight);
						downBlocks.getFirst().getRectangle().setHeight(2*blockHeight);
					} else {
						if(downBlocks.getFirst().isTrap()) {
							downBlocks.getFirst().setCrack(2);
						} else {
							downBlocks.getFirst().setCrack(0);
						}
						downBlocks.getFirst().getRectangle().setY(0);
						downBlocks.getFirst().getRectangle().setHeight(blockHeight);
					}
					score -= 3 ;
					isBlockCreating = false;
				}

				if(upBlocks.getLast().getRectangle().getX() > maxX) {
					isBlockCreating = true;
					last = upBlocks.getFirst().getRectangle().getX();
					upBlocks.addFirst(upBlocks.removeLast());
					upBlocks.getFirst().getRectangle().setX(last - 4 * blockWidth);
					if(upBlocks.getFirst().isHole()) {
						upBlocks.getFirst().getRectangle().setY(maxY + blockHeight);
						upBlocks.getFirst().getRectangle().setHeight(-2*blockHeight);
					} else {
						if(upBlocks.getFirst().isTrap()) {
							upBlocks.getFirst().setCrack(2);
						} else {
							upBlocks.getFirst().setCrack(0);
						}
						upBlocks.getFirst().getRectangle().setY(maxY);
						upBlocks.getFirst().getRectangle().setHeight(-blockHeight);
					}
					isBlockCreating = false;
				}
			}

			if(enemy.isActive() == false) {
				if (downBlocks.getFirst().getRectangle().getX() < maxX/5-3* ballWidth) {
					isBlockCreating = true;
					last = downBlocks.getLast().getRectangle().getX();
					downBlocks.addLast(downBlocks.removeFirst());
					downBlocks.getLast().getRectangle().setX(last + 4 * blockWidth);
					if(downBlocks.getLast().isHole()) {
						downBlocks.getLast().getRectangle().setY(-blockHeight);
						downBlocks.getLast().getRectangle().setHeight(2*blockHeight);
					} else {
						if(downBlocks.getLast().isTrap()) {
							downBlocks.getLast().setCrack(2);
						} else {
							downBlocks.getLast().setCrack(0);
						}
						downBlocks.getLast().getRectangle().setY(0);
						downBlocks.getLast().getRectangle().setHeight(blockHeight);
					}
					if(bonus.isActive()) {
						score += 8;
					}

					isBlockCreating = false;
				}
				if(upBlocks.getFirst().getRectangle().getX() < maxX/5-3* ballWidth) {
					isBlockCreating = true;
					last = upBlocks.getLast().getRectangle().getX();
					upBlocks.addLast(upBlocks.removeFirst());
					upBlocks.getLast().getRectangle().setX(last + 4*blockWidth);
					if(upBlocks.getLast().isHole()) {
						upBlocks.getLast().getRectangle().setY(maxY + blockHeight);
						upBlocks.getLast().getRectangle().setHeight(-2*blockHeight);
					} else {
						if(upBlocks.getLast().isTrap()) {
							upBlocks.getLast().setCrack(2);
						} else {
							upBlocks.getLast().setCrack(0);
						}
						upBlocks.getLast().getRectangle().setY(maxY);
						upBlocks.getLast().getRectangle().setHeight(-blockHeight);
					}
					isBlockCreating = false;
				}
			}

			if(Gdx.input.getDeltaY() > 50 && ball.getCircle().y + 2* ball.getCircle().radius < maxY ) {
				if(reverse) {
					reverse = false;
					floor = 0;
					mov = 0;
				}
			}
			if(Gdx.input.getDeltaY() < -50 && ball.getCircle().y + ball.getCircle().radius > 0) {
				if(reverse == false) {
					reverse = true;
					floor = maxY;
					mov = 0;
				}
			}

			Iterator iterator = downBlocks.iterator();
			while (iterator.hasNext()) {
				Block block = (Block) iterator.next();
				iterateBlocks(block);
			}

			Iterator iterator2 = upBlocks.iterator();
			while (iterator2.hasNext()) {
				Block block = (Block) iterator2.next();
				iterateBlocks(block);
			}

			if(isCracked) {
				recreate(temp);
				isCracked = false;
			}

			batch.draw(ball.getTexture(), ball.getCircle().x - ball.getCircle().radius, ball.getCircle().y - ball.getCircle().radius,
					2*ball.getCircle().radius, 2*ball.getCircle().radius);
			batch.draw(bonus.getTexture(), bonus.getCircle().x - bonus.getCircle().radius, bonus.getCircle().y- bonus.getCircle().radius,
					2*bonus.getCircle().radius, 2*bonus.getCircle().radius);
			batch.draw(enemy.getTexture(), enemy.getCircle().x - enemy.getCircle().radius, enemy.getCircle().y - enemy.getCircle().radius,
					2*enemy.getCircle().radius, 2*enemy.getCircle().radius);

			highScoreText.draw(batch, "High Score: " + highScore,  ballWidth/2, maxY - ballWidth/2);
			scoreText.draw(batch, String.valueOf(score), ballWidth/2, maxY - ballWidth);


			if(meteor.isActive()) {
				if(meteor.getEffect().isComplete()) {
					meteor.getEffect().reset();
				}
				meteor.getEffect().getEmitters().first().setPosition(meteor.getCircle().x - 3*meteorRadius/2, meteor.getCircle().y);
				meteor.getEffect().draw(batch);
			}

			if(meteor2.isActive()) {
				System.out.println(meteor2.getCircle().x);
				meteor2.getEffect().getEmitters().first().setPosition(meteor2.getCircle().x - 3*meteorRadius/2, meteor2.getCircle().y);
				meteor2.getEffect().draw(batch);
				if(meteor2.getEffect().isComplete()) {
					meteor2.getEffect().reset();
				}
			}

			if(meteor3.isActive()) {
				meteor3.getEffect().getEmitters().first().setPosition(meteor3.getCircle().x - 3*meteorRadius/2, meteor3.getCircle().y);
				meteor3.getEffect().draw(batch);
				if(meteor3.getEffect().isComplete()) {
					meteor3.getEffect().reset();
				}
			}

			if(sizeBonus.isHoleSwell() || sizeBonus.isHoleReady() || sizeBonus.isBlow()) {
				batch.draw(sizeBonus.getTexture(), sizeBonus.getCircle().x - sizeBonus.getCircle().radius, sizeBonus.getCircle().y - sizeBonus.getCircle().radius,
						2* sizeBonus.getCircle().radius, 2* sizeBonus.getCircle().radius);
				sizeBonus.getCircle().setX(sizeBonus.getCircle().x - velocity);
				if(sizeBonus.getCircle().radius >= ballWidth/4) {
					if(sizeBonusEffect.isComplete()) {
						sizeBonusEffect.reset();
					}
					sizeBonusEffect.getEmitters().first().setPosition(sizeBonus.getCircle().x, sizeBonus.getCircle().y);
					sizeBonusEffect.draw(batch);
					sizeBonusEffect.update(Gdx.graphics.getDeltaTime());
					if(sizeBonus.getCircle().x + 3*ballWidth < 0) {
						sizeBonus.setHoleSwell(false);
						sizeBonus.setHoleReady(false);
						sizeBonusCount = 0;
					}
				}
			}

			if(effectedBlock.isActive()) {
				if(effectedBlock.getType().equals("trap")) {
					if(effectedBlock.getSide().equals("upBlocks")) {
						trapEffect.getEmitters().first().setPosition(ball.getCircle().x, maxY);
					} else {
						trapEffect.getEmitters().first().setPosition(ball.getCircle().x,0);
					}
					trapEffect.draw(batch);
					trapEffect.update(Gdx.graphics.getDeltaTime());
				} else {
					if(effectedBlock.getSide().equals("upBlocks")) {
						blockEffect.getEmitters().first().setPosition(ball.getCircle().x, maxY);
					} else {
						blockEffect.getEmitters().first().setPosition(ball.getCircle().x, 0 );
					}
					blockEffect.draw(batch);
					blockEffect.update(Gdx.graphics.getDeltaTime());
				}
			}

			if(ball.isBurst() == false && isOver == false) {
				if(ball.getBallEffect().isComplete()) {
					ball.getBallEffect().reset();
				}
				ball.getBallEffect().getEmitters().first().setPosition(ball.getCircle().x - 2*velocity, ball.getCircle().y);
				ball.getBallEffect().draw(batch);
				ball.getBallEffect().update(Gdx.graphics.getDeltaTime());
			}
			if(isOver) {
				gameOverText.draw(batch, "Play again", Gdx.graphics.getWidth()/3, Gdx.graphics.getHeight()/2);

				if(isAddingAdTime) {
					adTime++;
					isAddingAdTime = false;
				}

				if(ball.isBurst()) {
					ball.getExplode().getEmitters().first().setPosition(ball.getCircle().x, ball.getCircle().y);
					ball.getExplode().draw(batch);
					ball.getExplode().update(Gdx.graphics.getDeltaTime());

				}
				if(highScore < score) {
					preferences.putInteger("highScore", score);
					preferences.flush();
				}
				if(Gdx.input.justTouched()) {
					gameTimer.cancel();
					gameTimer.purge();
					bonusTimer.cancel();
					bonusTimer.purge();
					status = Status.READY;
					gameCreate();
					if(adTime == 3) {
						adTime = 0;
						adsService.showAd();
					}
				}
			}
		}
		batch.end();
	}

	@Override
	public void dispose () {

	}

	public void createNewBlock(int i, Deque q, String type) {
		Block block = new Block();
		if(type.equals("up")) {
			block.setRectangle(new Rectangle((maxX/4) + i*4*blockWidth, maxY, blockWidth, -blockHeight));
			block.setInWhich("upBlocks");
			if(i == 4 || i == 8) {
				block.getRectangle().setPosition(block.getRectangle().getX(), maxY + blockHeight);
				block.getRectangle().setHeight(-2*blockHeight);
				block.setTexture(holeBlock);
				block.setHole(true);
			}
			else if(i == 1 || i == 7) {
				block.setTexture(trapBlock);
				block.setTrap(true);
				block.setCrack(2);
				if(random.nextInt(2) == 1) {
					block.setTrapActive(true);
				}
			} else if(i == 6 || i == 11) {
				block.setSpike(true);
				block.setCrack(0);
				block.setTexture(spikeBlock);
			} else {
				block.setCrack(0);
				block.setTexture(redBlock);
			}
			if( i == 5 || i == 9) {

			}
		} else {
			block.setRectangle(new Rectangle((maxX/5) + i*4*blockWidth, 0, blockWidth, blockHeight));
			block.setInWhich("downBlocks");
			if(i == 7  || i == 12) {
				block.getRectangle().setPosition(block.getRectangle().getX(), -blockHeight);
				block.getRectangle().setHeight(2*blockHeight);
				block.setTexture(holeBlock);
				block.setHole(true);
			}
			else if(i == 3 || i == 10) {
				block.setTexture(trapBlock);
				block.setTrap(true);
				block.setCrack(2);
				if(random.nextInt(2) == 1) {
					block.setTrapActive(true);
				}
			} else if(i == 2 || i == 9) {
				block.setCrack(0);
				block.setSpike(true);
				block.setTexture(spikeBlock);
			} else {
				block.setCrack(0);
				block.setTexture(redBlock);
			}
		}
		q.add(block);
	}

	public void recreate(Block block) {
		if(block.getInWhich().equals("upBlocks")) {
			last = upBlocks.getLast().getRectangle().getX();
			if(upBlocks.getFirst().isTrap()) {
				upBlocks.getFirst().setCrack(2);
			} else {
				upBlocks.getFirst().setCrack(0);
			}
			upBlocks.addLast(upBlocks.removeFirst());
			upBlocks.getLast().getRectangle().setPosition(last + 4 * blockWidth, maxY);
			upBlocks.getLast().getRectangle().setSize(blockWidth, -blockHeight);
		} else {
			last = downBlocks.getLast().getRectangle().getX();
			if(downBlocks.getFirst().isTrap()) {
				downBlocks.getFirst().setCrack(2);
			} else {
				downBlocks.getFirst().setCrack(0);
			}
			downBlocks.addLast(downBlocks.removeFirst());
			downBlocks.getLast().getRectangle().setPosition(last + 4 * blockWidth, 0);
			downBlocks.getLast().getRectangle().setSize( blockWidth, blockHeight);
		}
		temp = null;
	}

	public void iterateBlocks(Block block) {
		block.getRectangle().setX(block.getRectangle().getX() - velocity);
		if(block.isTrap()) {
			if(block.getCrack() == 3) {
				isCracked = true;
				score += 5;
				if(block.isTrapActive()) {
					if(reverse) {
						reverse = false;
						floor = 0;
						mov = 0;
					} else {
						reverse = true;
						floor = maxY;
						mov = 0;
					}
				}
			}
		}
		else {
			if (block.getInWhich().equals("upBlocks")) {
				if(block.getCrack() == 1) {
					block.getRectangle().setHeight(-2*blockHeight/3);
				}
				if(block.getCrack() == 2) {
					block.getRectangle().setHeight(-blockHeight/3);
				}
				if(block.getCrack() == 3) {
					isCracked = true;
				}
			} else {
				if(block.getCrack() == 1) {
					block.getRectangle().setHeight(2*blockHeight/3);
				}
				if(block.getCrack() == 2) {
					block.getRectangle().setHeight(blockHeight/3);
				}
				if(block.getCrack() == 3) {
					isCracked = true;
				}
			}
		}
		batch.draw(block.getTexture(), block.getRectangle().getX(), block.getRectangle().getY(),
				block.getRectangle().getWidth(), block.getRectangle().getHeight());
	}

	public boolean intersect(Deque q) {
		boolean result = false;
		if(isCracked == false && isBlockCreating == false) {
			Iterator iterator = q.iterator();
			while(iterator.hasNext()) {
				Block block = (Block) iterator.  next();
				if(ball.getCircle().x + ball.getCircle().radius-1 > block.getRectangle().getX() &&
						ball.getCircle().x - ball.getCircle().radius - 1 < block.getRectangle().getX() + blockWidth) {
					temp = block;
					if(reverse) {
						if(block.isTrap()) {
							floor = maxY - blockHeight - ballWidth/2;
						} else {
							if(block.getCrack() == 1) {
								floor = maxY - (2*blockHeight/3) - ballWidth/2;
							} else if(block.getCrack() == 2) {
								floor = maxY - (1*blockHeight/3) - ballWidth/2;
							} else {
								floor = maxY - blockHeight - ballWidth/2;
							}
						}
					} else {
						if (block.isTrap()) {
							floor = blockHeight + ballWidth/2;
						} else {
							if (block.getCrack() == 1) {
								floor = (2 * blockHeight / 3) + ballWidth/2;
							} else if (block.getCrack() == 2) {
								floor = ballWidth/2 + blockHeight / 3;
							} else {
								floor = blockHeight + ballWidth/2;
							}
						}
					}
					result = true;
					break;
				}else {
					if(reverse) {
						floor = maxY;
					} else {
						floor = 0;
					}
					temp = null;
				}
			}
		}
		return result;
	}

	public void ballFall (){
		if(reverse) {
			if(intersect(upBlocks)) {
				if(mov > (floor - ball.getCircle().y)) {
					ball.getCircle().setY( floor + 1);
				} else {
					ball.getCircle().setY(ball.getCircle().y + mov);
				}
				if(ball.getCircle().y >= floor) {
					if(temp != null && temp.isHole()) {
						holeActive = true;
						isFalling = false;
					} else {
						if(temp != null) {
							if(temp.isSpike()) {
								ball.setBurst(true);
								ball.getExplode().reset();
								isOver = true;
								isAddingAdTime = true;
							} else {
								effectedBlock.setSide(temp.getInWhich());
								if(temp.isTrap()) {
									effectedBlock.setType("trap");
								} else {
									effectedBlock.setType("block");
								}
								if(temp.getCrack() == 2) {
									temp.setCrack(3);
									score += 5;
									effectedBlock.setActive(true);
								}
								if(temp.getCrack() == 1) {
									if(ball.getSize().equals(BallSize.SMALL)) {
										temp.setCrack(2);
										score += 3;
									} else {
										effectedBlock.setActive(true);
										temp.setCrack(3);
										score += 8;
									}
								}
								if(temp.getCrack() == 0) {
									if(ball.getSize().equals(BallSize.SMALL)) {
										temp.setCrack(1);
										score += 2;
									} else if(ball.getSize().equals(BallSize.MID)) {
										temp.setCrack(2);
										score += 5;
									} else {
										effectedBlock.setActive(true);
										temp.setCrack(3);
										score += 10;
									}
								}
							}
						}
						isFalling = false;
						isJumping = true;
						mov = maxBallVelocity;
					}
				}
				if(mov + gravity > maxBallVelocity) {
					mov = maxBallVelocity;
				} else {
					mov += gravity;
				}
			} else {
				if(mov + gravity > maxBallVelocity) {
					mov = maxBallVelocity;
				} else {
					mov += gravity;
				}
				if(ball.getCircle().y > maxY) {
					isOver = true;
					isAddingAdTime = true;
				}
				ball.getCircle().setY(ball.getCircle().y + mov);
			}
		} else {
			if(intersect(downBlocks)) {
				if(mov > ball.getCircle().y - floor) {
					ball.getCircle().setY(floor-1);
				}else {
					ball.getCircle().setY(ball.getCircle().y - mov);
				}
				if(ball.getCircle().y <= floor) {
					if(temp != null && temp.isHole()) {
						holeActive = true;
						isFalling = false;
					} else {
						if(temp != null) {
							if(temp.isSpike()) {
								isOver = true;
								isAddingAdTime = true;
								ball.setBurst(true);
								ball.getExplode().reset();
							} else {
								effectedBlock.setSide(temp.getInWhich());
								if(temp.isTrap()) {
									effectedBlock.setType("trap");
								} else {
									effectedBlock.setType("block");
								}
								if(temp.getCrack() == 2) {
									effectedBlock.setActive(true);
									temp.setCrack(3);
									score += 5;
								}
								if(temp.getCrack() == 1) {
									if(ball.getSize().equals(BallSize.SMALL)) {
										temp.setCrack(2);
										score += 3;
									} else {
										effectedBlock.setActive(true);
										temp.setCrack(3);
										score += 8;
									}
								}
								if(temp.getCrack() == 0) {
									if(ball.getSize().equals(BallSize.SMALL)) {
										temp.setCrack(1);
										score += 2;
									} else if(ball.getSize().equals(BallSize.MID)) {
										temp.setCrack(2);
										score += 5;
									} else {
										effectedBlock.setActive(true);
										temp.setCrack(3);
										score += 10;
									}
								}
							}
						}
						isFalling = false;
						isJumping = true;
						mov = maxBallVelocity;
					}
				}
				if(mov + gravity > maxBallVelocity) {
					mov = maxBallVelocity;
				} else {
					mov += gravity;
				}
			} else {
				if(ball.getCircle().y < floor) {
					isOver = true;
					isAddingAdTime = true;
				}
				if(mov + gravity > maxBallVelocity) {
					mov = maxBallVelocity;
				} else {
					mov += gravity;
				}
				ball.getCircle().setY(ball.getCircle().y - mov);
			}
		}
	}

	public void ballJump (){
		if(reverse) {
			if(mov >= gravity) {
				ball.getCircle().setY(ball.getCircle().y - mov);
				mov -= gravity;
				if (mov < 0) {
					mov = gravity;
				}
			} else {
				mov = 0;
				isJumping = false;
				isFalling = true;
			}
		} else {
			if(mov >= gravity) {
				ball.getCircle().setY(ball.getCircle().y + mov);
				mov -= gravity;
				if (mov < 0) {
					mov = gravity;
				}
			} else {
				mov = 0;
				isJumping = false;
				isFalling = true;
			}

		}
	}

	public void ballTimer (){
		gameTimer = new Timer();
		gameTimer.schedule(new TimerTask() {
			@Override
			public void run() {
				if(isOver) {
					velocity = 0;
					if(ball.isBurst()) {
						ball.getCircle().setRadius(0);
						ball.setEffectCount(ball.getEffectCount() + 1);
						if(ball.getEffectCount() == 60) {
							ball.setBurst(false);
							ball.setEffectCount(0);
						}
					}
				} else {
					if(bonus.isActive()) {
						bonusAndEnemyActive(bonus);
						enemy.getCircle().setRadius(0);
						sizeBonus.getCircle().setRadius(0);
						effectedBlock.setActive(false);
						effectedBlock.setCount(0);
					} else if(enemy.isActive()) {
						bonusAndEnemyActive(enemy);
						bonus.getCircle().setRadius(0);
						sizeBonus.getCircle().setRadius(0);
						effectedBlock.setActive(false);
						effectedBlock.setCount(0);
					} else {
						if(holeActive) {
							if(totalHoleVelocity < 10*blockWidth) {
								velocity = 60*gravity;
								totalHoleVelocity += velocity;
								if(reverse) {
									if(ball.getCircle().y >= 2*ball.getCircle().radius + maxY) {
										ball.getCircle().setY(maxY+2*ball.getCircle().radius);
									} else {
										ball.getCircle().setY(ball.getCircle().y + mov);
									}
								} else {
									if(ball.getCircle().y <= -2*ball.getCircle().radius) {
										ball.getCircle().setY(- 2*ball.getCircle().radius);
									} else {
										ball.getCircle().setY(ball.getCircle().y - mov);
									}
								}
							} else {
								velocity = 0;
								mov = 15*gravity;
								if(reverse) {
									ball.getCircle().setY(ball.getCircle().y - mov);
									if(ball.getCircle().y + ball.getCircle().radius < maxY) {
										mov = 15*gravity;
										isFalling = true;
										holeActive = false;
										reverse = false;
										totalHoleVelocity = 0;
									}
								} else {
									ball.getCircle().setY(ball.getCircle().y + mov);
									if(ball.getCircle().y + ball.getCircle().radius > 0) {
										mov = 12*gravity;
										isFalling = true;
										holeActive = false;
										reverse = true;
										totalHoleVelocity = 0;
									}
								}
							}
						} else {
							velocity = -3*Gdx.input.getDeltaX()/2;
						}
						enemy.getCircle().setX(enemy.getCircle().x + enemy.getVelocity());
						bonus.getCircle().setX(bonus.getCircle().x + bonus.getVelocity());
						if(meteor.getCircle().x < maxX) {
							meteor.setActive(true);
						}
						if(meteor.getCircle().x + 2 * meteorRadius < 0) {
							meteor.getCircle().setPosition(2*maxX, (maxY - 3*blockHeight)*random.nextFloat() + 3*blockHeight/2);
							meteor.setActive(false);
							meteor.getEffect().reset();
						} else {
							meteor.getCircle().setX(meteor.getCircle().x + meteor.getVelocity() - velocity/2);
						}
						if(meteor2.getCircle().x < maxX) {
							if(level2 || level3) {
								meteor2.setActive(true);
							}
						}
						if(meteor2.getCircle().x + 2 * meteorRadius < 0) {
							meteor2.getCircle().setPosition(3*maxX/2, (maxY - 3*blockHeight)*random.nextFloat() + 3*blockHeight/2);
						} else {
							if(level2 || level3) {
								meteor2.getCircle().setX(meteor2.getCircle().x + meteor2.getVelocity() - velocity/2);
							}
						}
						if(meteor3.getCircle().x < maxX) {
							if(level3) {
								meteor3.setActive(true);
							}
						}
						if(meteor3.getCircle().x + 2 * meteorRadius < 0) {
							meteor3.getCircle().setPosition(4*maxX, (maxY - 3*blockHeight)*random.nextFloat() + 3*blockHeight/2);
						} else {
							if(level3) {
								meteor3.getCircle().setX(meteor3.getCircle().x + meteor3.getVelocity() - velocity/2);
							}
						}
						if(isFalling && holeActive == false) {
							ballFall();
						}
						if(isJumping && holeActive == false) {
							ballJump();
						}
						if( bonus.isSwell()) {
							bonusAndEnemySwell(bonus);
						}
						if(enemy.isSwell()) {
							bonusAndEnemySwell(enemy);
						}
						if(Intersector.overlaps(ball.getCircle(), meteor.getCircle())) {
							isOver = true;
							isAddingAdTime = true;
						}
						if(Intersector.overlaps(ball.getCircle(), meteor2.getCircle())) {
							isOver = true;
							isAddingAdTime = true;
						}
						if(Intersector.overlaps(ball.getCircle(), meteor3.getCircle())) {
							isOver = true;
							isAddingAdTime = true;
						}
						if(sizeBonus.isHoleSwell()) {
							if(sizeBonus.getCircle().radius >= 2*ballWidth/3) {
								sizeBonus.getCircle().setRadius(2*ballWidth/3 );
								sizeBonus.setHoleReady(true);
								sizeBonus.setHoleSwell(false);
							} else {
								sizeBonus.getCircle().setRadius(sizeBonus.getCircle().radius + 2*gravity);
							}
						}
						if(sizeBonus.isBlow()) {
							if(ball.getSize().equals(BallSize.SMALL)) {
								ball.getCircle().setRadius(ballWidth/3);
							}
							if(ball.getSize().equals(BallSize.MID)) {
								ball.getCircle().setRadius(ballWidth/2);
							}
							if(ball.getSize().equals(BallSize.LARGE)) {
								ball.getCircle().setRadius(2*ballWidth/3);
							}
							sizeBonus.setActive(false);
							if(ball.getCircle().y > hMax) {
								reverse = false;
							} else {
								reverse = true;
							}
							isFalling = true;
							isJumping = false;
							sizeBonus.setBlow(false);
						}
						if(sizeBonus.isHoleReady()) {
							if(Intersector.overlaps(ball.getCircle(), sizeBonus.getCircle())) {
								int i = random.nextInt(3);
								if(i == 0) {
									ball.setSize(BallSize.MID);
								} else if(i == 1){
									ball.setSize(BallSize.SMALL);
								} else {
									ball.setSize(BallSize.LARGE);
								}
								sizeBonus.getCircle().setRadius(0);
								sizeBonus.getCircle().setPosition(ball.getCircle().x, ball.getCircle().y);
								sizeBonus.setBlow(true);
								sizeBonus.setHoleReady(false);
								sizeBonus.setHoleSwell(false);
								mov = 0;
							}
						}
					}
					if(effectedBlock.isActive()) {
						if(effectedBlock.getCount() == 0) {
							blockEffect.reset();
							trapEffect.reset();
						}
						effectedBlock.setCount(effectedBlock.getCount() + 1);
						if(effectedBlock.getCount() == 55) {
							effectedBlock.setActive(false);
							effectedBlock.setCount(0);
							blockEffect.reset();
							trapEffect.reset();
						}
					}
				}
			}
		}, 0, period);
	}

	public void createBonusPosition(Bonus temp) {
		int i = random.nextInt(2);
		if(i == 0) {
			temp.getCircle().setPosition(random.nextFloat()*(maxX/ 4) + 2* ballWidth,
					maxY - (random.nextFloat() * (hMax - 3*blockHeight) + blockHeight));
		} else {
			temp.getCircle().setPosition(random.nextFloat()*(maxX/ 4) + 2* ballWidth,
					random.nextFloat() * (hMax - 3*blockHeight) + blockHeight);
		}
	}

	public void bonusAndEnemyTimer() {
		bonusTimer = new Timer();
		bonusTimer.schedule(new TimerTask() {
			@Override
			public void run() {
				if(sizeBonus.isBlow()) {
					bonus.setSwell(false);
					enemy.setSwell(false);
					velocity = 0;
				}
				else if(bonus.isActive()) {
					enemy.setSwell(false);
					enemy.getCircle().setRadius(0);
					sizeBonus.getCircle().setRadius(0);
					meteor.getCircle().setPosition(2*maxX, (maxY - 3*blockHeight)*random.nextFloat() + 3*blockHeight/2);
					meteor.setActive(false);
					meteor2.getCircle().setPosition(3*maxX/2, (maxY - 3*blockHeight)*random.nextFloat() + 3*blockHeight/2);
					meteor2.setActive(false);
					meteor3.getCircle().setPosition(4*maxX, (maxY - 3*blockHeight)*random.nextFloat() + 3*blockHeight/2);
					meteor3.setActive(false);
				} else if(enemy.isActive()) {
					bonus.setSwell(false);
					bonus.getCircle().setRadius(0);
					sizeBonus.getCircle().setRadius(0);
					meteor.getCircle().setPosition(2*maxX, (maxY - 3*blockHeight)*random.nextFloat() + 3*blockHeight/2);
					meteor.setActive(false);
					meteor2.getCircle().setPosition(3*maxX/2, (maxY - 3*blockHeight)*random.nextFloat() + 3*blockHeight/2);
					meteor2.setActive(false);
					meteor3.getCircle().setPosition(4*maxX, (maxY - 3*blockHeight)*random.nextFloat() + 3*blockHeight/2);
					meteor3.setActive(false);
				} else{
					bonusCount++;
					bonus.getCircle().setRadius(bonusRadius/2);
					enemy.getCircle().setRadius(bonusRadius/2);
					if(bonusCount == 3) {
						enemy.setVelocity(-enemy.getVelocity());
						bonus.setVelocity(-bonus.getVelocity());
						sizeBonusCount++;
					}
					if(bonusCount == 6) {
						enemy.setVelocity(-enemy.getVelocity());
						bonus.setVelocity(-bonus.getVelocity());
					}
					if(bonusCount == 7) {
						createBonusPosition(bonus);
						createBonusPosition(enemy);
						bonus.getCircle().setRadius(0);
						enemy.getCircle().setRadius(0);
					}
					if(bonusCount == 11) {
						bonusCount = 0;
						bonus.getCircle().setRadius(bonusRadius/2);
						createBonusPosition(bonus);
						enemy.getCircle().setRadius(bonusRadius/2);
						createBonusPosition(enemy);
					}
					if(bonusCount <= 7 && bonusCount >= 3) {
						int i = random.nextInt(15);
						if(i > bonusRandom ) {
							bonus.setSwell(true);
							enemy.setSwell(false);
						}
						else if(i < enemyRandom ) {
							enemy.setSwell(true);
							bonus.setSwell(false);
						} else {
							enemy.setSwell(false);
							bonus.setSwell(false);
						}
					}
					if(sizeBonusCount == 3) {
						sizeBonusCount++;
						if(random.nextInt(2) == 1) {
							sizeBonus.getCircle().setPosition(maxX - 3*ballWidth, maxY - 3*ballWidth);
						} else {
							sizeBonus.getCircle().setPosition(maxX - 3*ballWidth, 3*ballWidth);
						}
						sizeBonus.getCircle().setRadius(0);
						sizeBonus.setHoleSwell(true);
					}
				}
				time++;
				if(time == 60) {
					calculateGravity(4000);
					bonus.setVelocity(10*gravity);
					enemy.setVelocity(-10*gravity);
					meteor.setRatio(1500*gravity);
					meteor.setVelocity(-maxY/meteor.getRatio());
				}
				if(time == 150) {
					calculateGravity(3500);
					bonusRandom = 10;
					enemyRandom = 3;
					bonus.setVelocity(12*gravity);
					enemy.setVelocity(-12*gravity);
					meteor.setRatio(1200*gravity);
					meteor.setVelocity(-maxY/meteor.getRatio());
					meteor2.setRatio(1200*gravity);
					meteor2.setVelocity(-maxY/meteor2.getRatio());
					meteor2.getEffect().start();
					level2 = true;
				}
				if(time == 240) {
					calculateGravity(3000);
					bonus.setVelocity(14*gravity);
					enemy.setVelocity(-14*gravity);
					meteor.setRatio(1000*gravity);
					meteor.setVelocity(-maxY/meteor.getRatio());
					meteor2.setRatio(1000*gravity);
					meteor2.setVelocity(-maxY/meteor2.getRatio());
					meteor3.setRatio(1000*gravity);
					meteor3.setVelocity(-maxY/meteor3.getRatio());
					meteor3.getEffect().start();
					level3 = true;
				}
			}
		}, 0, 1000);
	}

	public void bonusAndEnemySwell(Bonus temp) {
		temp.getCircle().setX(temp.getCircle().x + temp.getVelocity());
		temp.getCircle().setRadius(temp.getCircle().radius + 7 *gravity);
		if(temp.getCircle().radius >= ball.getCircle().radius) {
			if(Intersector.overlaps(ball.getCircle(), temp.getCircle())) {
				temp.setActive(true);
				if(temp.getType().equals("bonus")) {
					velocity = 3*maxBallVelocity;
				} else {
					velocity = -(3*maxBallVelocity);
				}
				temp.getCircle().setRadius(ball.getCircle().radius);
				temp.getCircle().setPosition(ball.getCircle().x, ball.getCircle().y);

				meteor.getCircle().setPosition( 2*maxX, (maxY - 3*blockHeight)*random.nextFloat() + 3*blockHeight/2);
				meteor.setActive(false);
				meteor2.getCircle().setPosition( 3*maxX/2, (maxY - 3*blockHeight)*random.nextFloat() + 3*blockHeight/2);
				meteor2.setActive(false);
				meteor3.getCircle().setPosition( 4*maxX, (maxY - 3*blockHeight)*random.nextFloat() + 3*blockHeight/2);
				meteor3.setActive(false);
				sizeBonus.getCircle().set(maxX + 3*ballWidth, maxY + 3*ballWidth, 0);
			}
		}
		if(temp.getCircle().radius >= ballWidth) {
			createBonusPosition(temp);
			temp.setSwell(false);
			temp.getCircle().setRadius(0);
			bonusCount = 0;
		}
	}

	public void bonusAndEnemyActive(Bonus temp) {
		temp.setTime(temp.getTime() + 30);
		if(temp.getCircle().y > hMax) {
			temp.getCircle().setY(temp.getCircle().y - bonusRadius /4);
		}
		if(temp.getCircle().y < hMax){
			temp.getCircle().setY(temp.getCircle().y + bonusRadius /4);
		}
		ball.getCircle().setY(temp.getCircle().y);
		if(temp.getTime() > 5100 ) {
			if(temp.getType().equals("bonus")) {
				velocity -= 6*gravity;
			} if(temp.getType().equals("enemy")) {
				velocity += 6*gravity;
			}
			temp.getCircle().setRadius(temp.getCircle().radius - hMax/126);
		}
		if(temp.getTime() >= 6000) {
			createBonusPosition(temp);
			temp.getCircle().setY(hMax);
			temp.getCircle().setRadius(0);
			bonus.setSwell(false);
			bonus.setActive(false);
			enemy.setSwell(false);
			enemy.setActive(false);
			temp.setTime(0);
			bonusCount = 0;
			sizeBonusCount = 0;
			mov = 0;
			reverse = false;
			isFalling = true;
			isJumping = false;
		}
	}
}
