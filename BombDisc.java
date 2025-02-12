public class BombDisc extends Disc{

    public BombDisc(Player player) {
        super(player);
    }

    @Override
    public void setOwner(Player player) {
        this.player=player;
    }

    @Override
    public String getType() {
        return "💣";
    }

}
