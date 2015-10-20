package dxw405.util;


public interface RandomGenerator
{
	RandomGenerator GEN_EMAIL_BHAM = new RandomBhamEmail();
	RandomGenerator GEN_EMAIL = new RandomEmail();
	RandomGenerator GEN_OFFICE = new RandomOffice();
	RandomGenerator GEN_POSTAL = new RandomAddress();

	String[] POSTAL_SUFFIXES = {"Road", "Lane", "Way", "Street"};
	String[] DOMAINS = {".com", ".net", ".co.uk", ".io", ".meme", ".gov", ".org"};

	/**
	 * Generates a random string of a length between the supplied min and max
	 *
	 * @param min Min length
	 * @param max Max length
	 * @return A String of n random characters
	 */
	static String randomString(int min, int max)
	{
		int n = Utils.RANDOM.nextInt((max - min) + 1) + min;

		char[] acc = new char[n];
		for (int i = 0; i < n; i++)
			acc[i] = (char) (Utils.RANDOM.nextInt(26) + 'a');

		return new String(acc);
	}

	String generate();

	class RandomBhamEmail implements RandomGenerator
	{
		private RandomBhamEmail()
		{
		}

		@Override
		public String generate()
		{
			return randomString(4, 8) + "@bham.ac.uk";
		}
	}

	class RandomEmail implements RandomGenerator
	{
		private RandomEmail()
		{
		}

		@Override
		public String generate()
		{
			return randomString(4, 8) + "@" + randomString(2, 5) + DOMAINS[Utils.RANDOM.nextInt(DOMAINS.length)];
		}
	}

	class RandomOffice implements RandomGenerator
	{
		private RandomOffice()
		{
		}

		@Override
		public String generate()
		{
			int floor = Utils.RANDOM.nextInt(5) + 1;
			int room = Utils.RANDOM.nextInt(300) + 50;

			return String.format("%d-%03d", floor, room);
		}
	}

	class RandomAddress implements RandomGenerator
	{
		private RandomAddress()
		{
		}

		@Override
		public String generate()
		{
			return String.format("%d %s %s", Utils.RANDOM.nextInt(500) + 1,
					Utils.capitalise(randomString(4, 8)),
					POSTAL_SUFFIXES[Utils.RANDOM.nextInt(POSTAL_SUFFIXES.length)]);
		}
	}
}